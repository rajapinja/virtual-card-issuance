package com.laraid.vci.ledger.service;

import com.laraid.vci.enums.EntryStatus;
import com.laraid.vci.enums.TransactionType;
import com.laraid.vci.ledger.dto.LedgerEntryDTO;
import com.laraid.vci.ledger.entity.LedgerEntry;
import com.laraid.vci.ledger.event.LedgerEntryEvent;
import com.laraid.vci.ledger.repo.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.laraid.vci.enums.Direction;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class LedgerService {

    private static final Logger log
            = LoggerFactory. getLogger(LedgerService.class);


    private final LedgerRepository repo;


    private final KafkaTemplate<String, LedgerEntryEvent> kafkaTemplate;

    public LedgerService(LedgerRepository repo, KafkaTemplate<String, LedgerEntryEvent> kafkaTemplate){
        this.repo = repo;
        this.kafkaTemplate = kafkaTemplate;

    }

    public void record(LedgerEntryDTO dto) {
        LedgerEntry entry = LedgerEntry.builder()
                .entryId(UUID.randomUUID())
                .walletId(dto.getWalletId())
                .transactionType(TransactionType.valueOf(dto.getTransactionType().toUpperCase()))
                .amount(dto.getAmount())
                .currency(dto.getCurrency())
                .direction(Direction.valueOf(dto.getDirection().toUpperCase()))
                .description(dto.getDescription())
                .timestamp(dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now())
                .correlationId(dto.getCorrelationId() != null ? dto.getCorrelationId() : UUID.randomUUID())
                .status(EntryStatus.valueOf(Optional.ofNullable(dto.getStatus()).orElse("POSTED").toUpperCase()))
                .build();

        LedgerEntry saved = repo.save(entry);
        log.info("Ledger entry saved: {}", saved);

        LedgerEntryEvent event = LedgerEntryEvent.builder()
                .entryId(saved.getEntryId())
                .walletId(saved.getWalletId())
                .amount(saved.getAmount())
                .currency(saved.getCurrency())
                .description(saved.getDescription())
                .timestamp(saved.getTimestamp())
                .build();

        kafkaTemplate.send("ledger.entry_created", event);
        log.info("Published LedgerEntryEvent: {}", event);
    }
}


