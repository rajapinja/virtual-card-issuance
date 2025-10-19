package com.laraid.vci.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardActivationRequest {
    private BigDecimal limit;
    private BigDecimal initialBalance;
}
