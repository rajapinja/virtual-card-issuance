package com.laraid.vci.card.event;

import com.laraid.vci.transaction.dto.SpendRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor              // 👈 Required for deserialization
@AllArgsConstructor
@Builder
public class SpendRequestEvent {
    private Long walletId;
    private Long merchantId;
    private BigDecimal amount;
    private String currency;

    // 👇 ADD THIS CONSTRUCTOR
    public SpendRequestEvent(SpendRequest request) {
        this.walletId = request.getWalletId();
        this.merchantId = request.getMerchantId();
        this.amount = request.getAmount();
        this.currency = request.getCurrency();
    }
}
