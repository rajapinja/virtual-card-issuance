package com.laraid.vci.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendRequest {

    @NotBlank(message = "Wallet ID is required")
    private Long walletId;

    @NotBlank(message = "Card ID is required")
    private String cardId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Merchant ID is required")
    private Long merchantId;

    @NotNull(message = "Request ID is required")
    private UUID requestId; // Correlate this spend event across ledger, audit, etc.

    private String description;


}


