package com.laraid.vci.cardstock.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


public class CardStockResponse {
    private String cardNumber;

    public CardStockResponse() {}

    public CardStockResponse(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}


