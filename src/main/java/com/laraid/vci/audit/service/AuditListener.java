package com.laraid.vci.audit.service;

import com.laraid.vci.audit.entity.AuditEntry;
import com.laraid.vci.audit.repo.AuditRepository;
import com.laraid.vci.ledger.event.LedgerEntryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditListener {

    private final AuditRepository auditRepository;

    @KafkaListener(topics = "#{kafkaTopicProperties.ledgerEntryCreated}", containerFactory = "auditKafkaListenerContainerFactory")
    public void auditLedger(LedgerEntryEvent event) throws Exception{
        AuditEntry entry = AuditEntry.builder()
                .entryId(event.getEntryId())
                .walletId(event.getWalletId())
                .transactionType(event.getTransactionType())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .direction(event.getDirection())
                .description(event.getDescription())
                .timestamp(event.getTimestamp())
                .correlationId(event.getCorrelationId())
                .status(event.getStatus())
                .receivedAt(Instant.now())
                .build();

        auditRepository.save(entry);
    }
}
