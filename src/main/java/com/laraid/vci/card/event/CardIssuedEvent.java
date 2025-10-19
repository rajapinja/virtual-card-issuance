package com.laraid.vci.card.event;

import com.laraid.vci.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ------------ 8. common-lib (DTOs + Kafka Events) ------------
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardIssuedEvent {
    private String cardNumber;
    private String cardholderName;
    private Long walletId;
    private String last4;
    private LocalDateTime issuedAt;
    private CardStatus status; // ISSUED
}