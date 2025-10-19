package com.laraid.vci.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SpendApprovedEvent {
    private UUID requestId;
    private Long walletId;
    private String cardId;
    private BigDecimal amount;
    private String merchantId;

    private String currency;        // ✅ Add this
    private String description;     // ✅ And this
}

