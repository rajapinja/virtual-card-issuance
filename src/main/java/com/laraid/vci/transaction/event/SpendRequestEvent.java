package com.laraid.vci.transaction.event;

import com.laraid.vci.transaction.dto.SpendRequest;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpendRequestEvent {

    private UUID requestId;
    private String cardId;
    private Long walletId;
    private BigDecimal amount;
    private String currency;
    private String description;

    // Optional factory method instead of constructor
    public static SpendRequestEvent from(SpendRequest request) {
        return SpendRequestEvent.builder()
                .requestId(request.getRequestId() != null
                        ? request.getRequestId()
                        : UUID.randomUUID())
                .cardId(request.getCardId())
                .walletId(request.getWalletId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .build();
    }
}
