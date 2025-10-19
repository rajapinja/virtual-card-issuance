package com.laraid.vci.transaction.service;

import com.laraid.vci.card.entity.Card;
import com.laraid.vci.card.repo.CardRepository;
import com.laraid.vci.enums.CardStatus;
import com.laraid.vci.ledger.service.LedgerListener;
import com.laraid.vci.transaction.event.SpendDeclinedEvent;
import com.laraid.vci.transaction.event.SpendRequestEvent;
import com.laraid.vci.wallet.dto.SpendApprovedEvent;
import com.laraid.vci.wallet.entity.Wallet;
import com.laraid.vci.wallet.repo.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class SpendRequestListener {

    private static final Logger log
            = LoggerFactory. getLogger(SpendRequestListener.class);


    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "#{kafkaTopicProperties.spendRequested}", containerFactory = "walletKafkaListenerContainerFactory")
    public void processSpend(SpendRequestEvent event) {
        log.info("Received spend request: {}", event);

        // Step 1: Fetch Card and validate status
        Card card = null;
        try {
            Long cardId = Long.parseLong(event.getCardId());
            card = cardRepository.findById(cardId).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("Invalid card ID format: {}", event.getCardId());
            kafkaTemplate.send("txn.spend_declined", SpendDeclinedEvent.builder()
                    .requestId(event.getRequestId())
                    .reason("INVALID_CARD_ID")
                    .build());
            return;
        }
        if (card == null || card.getStatus() != CardStatus.ISSUED) {
            log.warn("Card not found or inactive: {}", event.getCardId());
            kafkaTemplate.send("txn.spend_declined", SpendDeclinedEvent.builder()
                    .requestId(event.getRequestId())
                    .reason("CARD_NOT_ACTIVE")
                    .build());
            return;
        }

        // Step 2: Check wallet existence
        Wallet wallet = walletRepository.findById(event.getWalletId()).orElse(null);
        if (wallet == null) {
            log.error("Wallet not found: {}", event.getWalletId());
            kafkaTemplate.send("txn.spend_declined", SpendDeclinedEvent.builder()
                    .requestId(event.getRequestId())
                    .reason("WALLET_NOT_FOUND")
                    .build());
            return;
        }

        // Step 3: Balance check
        BigDecimal balance = wallet.getCurrentBalance();
        if (balance.compareTo(event.getAmount()) < 0) {
            log.warn("Insufficient balance for wallet {}: {}", event.getWalletId(), balance);
            kafkaTemplate.send("txn.spend_declined", SpendDeclinedEvent.builder()
                    .requestId(event.getRequestId())
                    .reason("INSUFFICIENT_BALANCE")
                    .build());
            return;
        }

        // Step 4: Deduct and persist
        wallet.setCurrentBalance(balance.subtract(event.getAmount()));
        walletRepository.save(wallet);

        // Step 5: Emit approval
        kafkaTemplate.send("txn.spend_approved", SpendApprovedEvent.builder()
                .requestId(event.getRequestId())
                .walletId(wallet.getId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .description(event.getDescription())
                .build());

        log.info("Spend approved and wallet updated for requestId: {}", event.getRequestId());
    }
}

