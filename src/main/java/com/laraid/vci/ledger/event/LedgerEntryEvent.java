package com.laraid.vci.ledger.event;

import com.laraid.vci.enums.Direction;
import com.laraid.vci.enums.EntryStatus;
import com.laraid.vci.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryEvent {

    private UUID entryId;
    private Long walletId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String currency;
    private Direction direction;
    private String description;
    private Instant timestamp;
    private UUID correlationId;
    private EntryStatus status;
}

