package com.laraid.vci.mockbank.dto;

import lombok.Builder;

import java.time.LocalDate;


@Builder
public record MockBankCardResponse(
        String pan,
        String cvv,
        String expiry,
        String cardToken
) {}

