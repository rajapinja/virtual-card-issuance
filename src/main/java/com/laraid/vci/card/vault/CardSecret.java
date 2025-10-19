package com.laraid.vci.card.vault;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CardSecret {

    private final String cardNumber;
    private final String cvv;
    private final String expiry;

    public CardSecret(String cardNumber, String cvv, String expiry) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiry = expiry;
    }

    public String getCardNumber() { return cardNumber; }
    public String getCvv() { return cvv; }
    public String getExpiry() { return expiry; }
}
