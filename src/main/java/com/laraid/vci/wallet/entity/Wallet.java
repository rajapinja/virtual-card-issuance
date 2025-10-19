package com.laraid.vci.wallet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
    @Id
    @GeneratedValue
    private Long id;
    private String merchantId;
    private String currency;
    private Long cardholderId; // or userId/merchantId

    private BigDecimal totalFund;    // Total funded
    private BigDecimal currentBalance;  // Remaining balance

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Wallet(@NotBlank(message = "Wallet Id must not be blank") Long walletId, String merchantId, @NotBlank(message = "Currency must not be blank") String currency, @NotBlank(message = "Card Holder Id must not be blank") Long cardholderId, @NotNull(message = "Amount must not be null") @Min(value = 1, message = "Amount must be greater than zero") BigDecimal amount, String reference) {
    }
}