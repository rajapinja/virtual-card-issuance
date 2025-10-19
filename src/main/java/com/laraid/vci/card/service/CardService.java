package com.laraid.vci.card.service;

import com.laraid.vci.card.client.CardStockClient;
import com.laraid.vci.card.dto.MockBankCardRequest;
import com.laraid.vci.card.client.MockBankClient;
import com.laraid.vci.card.cardlimit.LimitCalculatorService;
import com.laraid.vci.card.client.CardPrintingClient;
import com.laraid.vci.card.dto.CardActivationRequest;
import com.laraid.vci.card.dto.CardIssueRequest;
import com.laraid.vci.card.dto.CardResponse;
import com.laraid.vci.card.entity.Card;
import com.laraid.vci.card.repo.CardRepository;
import com.laraid.vci.card.vault.InMemoryTokenVaultService;
import com.laraid.vci.enums.CardStatus;
import com.laraid.vci.enums.CardType;
import com.laraid.vci.enums.CurrencyType;
import com.laraid.vci.enums.EmployerType;
import com.laraid.vci.utils.EncryptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.laraid.vci.card.event.CardIssuedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;



@Service
@RequiredArgsConstructor
public class CardService {

    private static final Logger log
            = LoggerFactory. getLogger(CardService.class);


    private final CardRepository cardRepository;
    private final KafkaTemplate<String, CardIssuedEvent> kafkaTemplate;
    private final MockBankClient mockBankClient;
    private final LimitCalculatorService limitCalculatorService;
    private final CardPrintingClient cardPrintingClient;
    private final EncryptionService encryptionService;
    private final CardStockClient cardStockClient;
    private final InMemoryTokenVaultService inMemoryTokenVaultService;


    @Transactional
    public CardResponse activateCard(Long id, CardActivationRequest request) {
        Card card = findCard(id);

        if (card.getStatus() != CardStatus.ISSUED) {
            throw new IllegalStateException("Card must be in ISSUED status to activate.");
        }

        card.setStatus(CardStatus.ACTIVE);
        card.setActivatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        card.setCurrentBalance(request.getInitialBalance());// or separate field if different
        card.setBalanceLimit(request.getLimit());
        card.setStatus(CardStatus.ACTIVE);

        return toCardResponse(cardRepository.save(card));

    }

    public Card blockCard(Long id) {
        Card card = findCard(id);
        validateStateForTransition(card.getStatus(), CardStatus.BLOCKED);
        card.setStatus(CardStatus.BLOCKED);
        return cardRepository.save(card);
    }

    public Card suspendCard(Long id) {
        Card card = findCard(id);
        validateStateForTransition(card.getStatus(), CardStatus.SUSPENDED);
        card.setStatus(CardStatus.SUSPENDED);
        return cardRepository.save(card);
    }

    public Card closeCard(Long id) {
        Card card = findCard(id);
        if (card.getStatus().isTerminal()) {
            throw new IllegalStateException("Card is already closed or expired.");
        }
        card.setStatus(CardStatus.CLOSED);
        return cardRepository.save(card);
    }

    private void validateStateForTransition(CardStatus from, CardStatus to) {
        if (from == CardStatus.CLOSED || from == CardStatus.EXPIRED) {
            throw new IllegalStateException("Invalid transition from " + from + " to " + to);
        }
    }

    private Card findCard(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card not found"));
    }

    public Mono<CardResponse> issueCard(CardIssueRequest request, String jwtToken) {
        return switch (request.getCardType()) {
            case "VIRTUAL" -> issueVirtualCard(request,jwtToken);
            case "PHYSICAL" -> issuePhysicalCard(request,jwtToken);
            default -> Mono.error(new IllegalArgumentException("Unsupported card type"));
        };
    }

    private Mono<CardResponse> issueVirtualCard(CardIssueRequest request, String jwtToken) {
        log.info("Issuing virtual card for {}", request.getCardholderName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dob = LocalDate.parse(request.getDob(), formatter);

        // Step 1: Generate unique virtual card number (this will go as PAN to mock bank)
        String virtualCardNumber = "VC" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        // Step 2: Calculate balance limit
        BigDecimal balLimit = limitCalculatorService.calculateLimit(request.getMonthlySalary());

        // Step 3: Prepare mock bank request
        MockBankCardRequest mockBankCardRequest = new MockBankCardRequest(
                request.getCardholderName(),
                virtualCardNumber,
                request.getCurrency(),
                balLimit
        );

        // Step 4: Call mock bank and continue chain with response
        return mockBankClient.provisionCard(mockBankCardRequest, jwtToken)
                .flatMap(mockResponse -> {

                    // 🔐 Store unencrypted values in memory vault
                    // Store: card_number, expiry, cvv, cardholder_id, wallet_id
                    // Store sensitive info and get a secure token reference from vault
                    String secureCardToken = inMemoryTokenVaultService.storeCard(
                           // mockResponse.cardToken(),  // original token (optional, depends on how vault identifies it)
                            mockResponse.pan(),
                            mockResponse.cvv(),
                            mockResponse.expiry()
                    );

                    // Step 5: Build and persist the card
                    Card card = Card.builder()
                            .cardNumber(encryptionService.encrypt(mockResponse.pan()))
                            .cvv(encryptionService.encrypt(mockResponse.cvv()))
                            .validThrough(encryptionService.encrypt(mockResponse.expiry()))
                            .cardToken(mockResponse.cardToken())// or use mockResponse.cardToken()

                            .cardholderName(request.getCardholderName())
                            .dob(dob)
                            .email(request.getEmail())
                            .mobileNumber(request.getMobileNumber())
                            .address(request.getAddress())
                            .employerType(EmployerType.valueOf(request.getEmployerType()))
                            .companyName(request.getCompanyName())
                            .monthlySalary(request.getMonthlySalary())
                            .walletId(request.getWalletId())
                            .cardType(CardType.valueOf(request.getCardType().toUpperCase()))
                            .currency(CurrencyType.valueOf(request.getCurrency().toUpperCase()))
                            .balanceLimit(balLimit)
                            .kycVerified(false)
                            .governmentIdType(request.getGovernmentIdType())
                            .governmentId(request.getGovernmentId())
                            .status(CardStatus.ISSUED)  // optional if needed
                            .build();

                    // Step 6: Save and publish event
                    return Mono.fromCallable(() -> cardRepository.save(card))
                            .doOnSuccess(saved -> publishIssuedEvent(saved))
                            .map(this::toCardResponse);
                });
    }


    public Mono<CardResponse> issuePhysicalCard(CardIssueRequest request, String jwtToken) {
        log.info("Issuing physical card for {}", request.getCardholderName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dob = LocalDate.parse(request.getDob(), formatter);

        // Step 1: Pull card number from mock stock pool
        String physicalCardNumber = cardStockClient.allocateCardNumber(jwtToken).block();

        log.info("Card Number returned : {}",physicalCardNumber);

        // Step 2: Prepare request to bank
        BigDecimal limit = limitCalculatorService.calculateLimit(request.getMonthlySalary());

        // Step 3: Create MockBankCardRequest request
        MockBankCardRequest mockBankCardRequest = new MockBankCardRequest(
                request.getCardholderName(),
                physicalCardNumber,
                request.getCurrency(),
                limit
        );

        // Step 4: Call bank and continue chain
        return mockBankClient.provisionCard(mockBankCardRequest,jwtToken)
                .flatMap(mockResponse -> {
                    // Step 4: Trigger physical card print
                    cardPrintingClient.sendPrintRequest(request, physicalCardNumber);

                    // Step 5: Build card entity
                    Card card = Card.builder()
                            .cardNumber(encryptionService.encrypt(mockResponse.pan()))
                            .cardToken(mockResponse.cardToken())
                            .cvv(encryptionService.encrypt(mockResponse.cvv()))
                            .validThrough(encryptionService.encrypt(mockResponse.expiry()))

                            .cardholderName(request.getCardholderName())
                            .dob(dob)
                            .email(request.getEmail())
                            .mobileNumber(request.getMobileNumber())
                            .address(request.getAddress())
                            .employerType(EmployerType.valueOf(request.getEmployerType()))
                            .companyName(request.getCompanyName())
                            .monthlySalary(request.getMonthlySalary())
                            .walletId(request.getWalletId())
                            .cardType(CardType.valueOf(request.getCardType().toUpperCase()))
                            .currency(CurrencyType.valueOf(request.getCurrency().toUpperCase()))
                            .balanceLimit(limit)
                            .kycVerified(false)
                            .governmentIdType(request.getGovernmentIdType())
                            .governmentId(request.getGovernmentId())
                            .status(CardStatus.ISSUED)
                            .issuedAt(LocalDateTime.now())// optional if needed
                            .build();

                    return Mono.fromCallable(() -> cardRepository.save(card));
                            })
                                .doOnSuccess(saved -> publishIssuedEvent(saved))
                                .map(this::toCardResponse);
    }


    private void publishIssuedEvent(Card card) {

        String decrypted = encryptionService.decrypt(card.getCardNumber()).toString();
        String last4 = decrypted.replaceAll("\\s+", "").substring(decrypted.length() - 4);

        CardIssuedEvent event = CardIssuedEvent.builder()
                .cardNumber(encryptionService.decrypt(card.getCardNumber()))
                .walletId(card.getWalletId())
                .cardholderName(card.getCardholderName())
                .issuedAt(card.getIssuedAt())
                .last4(last4)
                .status(card.getStatus())
                .build();

        kafkaTemplate.send("txn.card_issued", event);
        log.info("CardIssuedEvent published: {}", event);
    }

    private CardResponse toCardResponse(Card card) {

        String decrypted = encryptionService.decrypt(card.getCardNumber()).toString();
        String last4 = decrypted.replaceAll("\\s+", "").substring(decrypted.length() - 4);

        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(encryptionService.decrypt(card.getCardNumber()))
                .cardholderName(card.getCardholderName())
                .validThrough(encryptionService.decrypt(card.getValidThrough()))
                .currency(card.getCurrency().name()) // Enum to String
                .cardType(card.getCardType().name()) // Enum to String
                .walletId(card.getWalletId())
                .status(card.getStatus().name()) // Enum to String
                .balanceLimit(card.getBalanceLimit())
                .currentBalance(card.getCurrentBalance())
                .issuedAt(card.getIssuedAt())
                .activatedAt(card.getActivatedAt())
                .updatedAt(card.getUpdatedAt())
                .mobileNumber(card.getMobileNumber())
                .email(card.getEmail())
                .governmentIdType(card.getGovernmentIdType().name())
                .governmentId(card.getGovernmentId())
                .monthlySalary(card.getMonthlySalary())
                .dob(card.getDob())
                .employerType(card.getEmployerType().name())
                .companyName(card.getCompanyName())
                .address(card.getAddress())
                .cvv(encryptionService.decrypt(card.getCvv()))
                .kycVerified(card.isKycVerified())
                .last4(last4)
                .currentBalance(card.getCurrentBalance())
                .issuedAt(LocalDateTime.now())// optional if needed
                .build();
    }


    public List<CardResponse> getAllCards(){

        try {
            List<CardResponse> cardResponseList= new ArrayList<>();

            for(Card card : cardRepository.findAll()){
                cardResponseList.add(toCardResponse(card));
            }
            return cardResponseList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
