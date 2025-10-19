package com.laraid.vci.card.dto;

import java.math.BigDecimal;

public record MockBankCardRequest(
        String cardholderName,
        String productCode,
        String currency,
        BigDecimal limit

    ) {}


