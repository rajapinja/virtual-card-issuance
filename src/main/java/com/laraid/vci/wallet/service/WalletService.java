package com.laraid.vci.wallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laraid.vci.enums.TransactionType;
import com.laraid.vci.utils.CurrentUserService;
import com.laraid.vci.wallet.dto.WalletFundRequest;
import com.laraid.vci.wallet.entity.IdempotencyKey;
import com.laraid.vci.wallet.entity.OutboxEvent;
import com.laraid.vci.wallet.entity.Wallet;
import com.laraid.vci.wallet.entity.WalletLedger;
import com.laraid.vci.wallet.repo.IdempotencyKeyRepository;
import com.laraid.vci.wallet.repo.OutboxEventRepository;
import com.laraid.vci.wallet.repo.WalletLedgerRepository;
import com.laraid.vci.wallet.repo.WalletRepository;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private static final Logger log
            = LoggerFactory. getLogger(WalletService.class);

    private final WalletRepository walletRepo;
    private final WalletLedgerRepository ledgerRepo;
    private final CurrentUserService currentUserService;
    private final OutboxEventRepository outboxEventRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Transactional
    public void fundWallet(String idempotencyKey, WalletFundRequest request) throws JsonProcessingException {

        String currentUserId = currentUserService.getCurrentUserId();
        log.info("Current User : {}", currentUserId);

        // 1. Check for idempotency
        if (idempotencyKeyRepository.existsById(idempotencyKey)) {
            throw new DuplicateRequestException("Duplicate fund request detected");
        }

        // 2. Wallet retrieval or creation
        Wallet wallet = walletRepo.findById(request.getWalletId())
                .orElseGet(() -> new Wallet(request.getWalletId(),
                        null, request.getCurrency(), null,
                        null, request.getReference()));

        // 3. Update balance
        wallet.setCurrentBalance(wallet.getTotalFund().add(request.getAmount())); // Be careful here
        walletRepo.save(wallet);

        // 4. Create ledger entry
        WalletLedger ledger = new WalletLedger();
        ledger.setUserId(currentUserId);
        ledger.setAmount(request.getAmount());
        ledger.setTxnType(TransactionType.CREDIT);
        ledgerRepo.save(ledger);

        // 5. Create outbox event
        ObjectMapper objectMapper = new ObjectMapper();
        String payloadJson = objectMapper.writeValueAsString(request);

        OutboxEvent event = OutboxEvent.builder()
                .aggregateId(request.getWalletId().toString())
                .eventType("WalletFundedEvent")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .payload(payloadJson)
                .build();

        outboxEventRepository.save(event);

        // 6. Save idempotency key
        IdempotencyKey record = IdempotencyKey.builder()
                .key(idempotencyKey)
                .userId(currentUserId)
                .createdAt(LocalDateTime.now())
                .build();

        idempotencyKeyRepository.save(record);

    }


    public BigDecimal getBalance(Long walletId) {
        return walletRepo.findById(walletId)
                .map(Wallet::getTotalFund)
                .orElse(BigDecimal.ZERO);
    }

    public List<WalletLedger> getLedger(String userId) {
        return ledgerRepo.findByUserId(userId);
    }
}
