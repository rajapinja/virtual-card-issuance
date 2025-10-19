package com.laraid.vci.ledger.service;

import com.laraid.vci.enums.EntryStatus;
import com.laraid.vci.enums.TransactionType;
import com.laraid.vci.ledger.entity.LedgerEntry;
import com.laraid.vci.ledger.repo.LedgerRepository;
import com.laraid.vci.wallet.dto.SpendApprovedEvent;
import com.laraid.vci.enums.Direction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class LedgerListener {

    private static final Logger log
            = LoggerFactory. getLogger(LedgerListener.class);

    @Autowired
    private LedgerRepository ledgerRepository;

    @KafkaListener(
            topics = "#{kafkaTopicProperties.spendApproved}",
            containerFactory = "ledgerKafkaListenerContainerFactory"
    )
    public void handleSpend(SpendApprovedEvent event) {
        log.info("Recording ledger entry for approved spend: {}", event);

        LedgerEntry entry = LedgerEntry.builder()
                .entryId(UUID.randomUUID())
                .walletId(event.getWalletId())
                .transactionType(TransactionType.DEBIT) // Debit transaction
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .direction(Direction.OUT) // Money going out
                .description(event.getDescription())
                .timestamp(Instant.now())
                .correlationId(event.getRequestId())
                .status(EntryStatus.POSTED)
                .build();

        ledgerRepository.save(entry);
        log.info("Ledger entry recorded for walletId={} with amount={}", event.getWalletId(), event.getAmount());
    }
}


