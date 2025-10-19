package com.laraid.vci.ledger.controller;

import com.laraid.vci.enums.Direction;
import com.laraid.vci.enums.EntryStatus;
import com.laraid.vci.enums.TransactionType;
import com.laraid.vci.ledger.dto.LedgerEntryDTO;
import com.laraid.vci.ledger.entity.LedgerEntry;
import com.laraid.vci.ledger.repo.LedgerRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    @Autowired
    private LedgerRepository repository;

    @PostMapping("/entries")
    public ResponseEntity<String> record(@Valid @RequestBody LedgerEntryDTO dto) {
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

        repository.save(entry);
        return ResponseEntity.ok("Ledger recorded successfully");
    }
}

