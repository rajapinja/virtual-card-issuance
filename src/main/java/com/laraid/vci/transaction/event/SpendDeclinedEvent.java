package com.laraid.vci.transaction.event;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendDeclinedEvent {

    private UUID requestId;
    private String walletId;
    private String reason; // e.g., "INSUFFICIENT_BALANCE", "WALLET_NOT_FOUND", "LIMIT_EXCEEDED"
    private String description; // Optional: extra info or debug message
    private String timestamp; // ISO 8601 format or Instant.now().toString() at emitter
}

