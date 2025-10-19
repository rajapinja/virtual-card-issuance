package com.laraid.vci.audit.entity;

import com.laraid.vci.enums.Direction;
import com.laraid.vci.enums.EntryStatus;
import com.laraid.vci.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEntry {

    @Id
    private UUID entryId;
    private Long walletId;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    private Direction direction;
    private String description;
    private Instant timestamp;
    private UUID correlationId;
    @Enumerated(EnumType.STRING)
    private EntryStatus status;

    private Instant receivedAt;
}
