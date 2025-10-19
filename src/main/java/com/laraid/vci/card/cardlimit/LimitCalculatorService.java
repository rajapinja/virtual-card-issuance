package com.laraid.vci.card.cardlimit;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LimitCalculatorService {

    public BigDecimal calculateLimit(BigDecimal salary) {
        if (salary == null) return BigDecimal.ZERO;

        if (salary.compareTo(BigDecimal.valueOf(25000)) < 0) {
            return BigDecimal.valueOf(5000);
        } else if (salary.compareTo(BigDecimal.valueOf(50000)) < 0) {
            return BigDecimal.valueOf(10000);
        } else if (salary.compareTo(BigDecimal.valueOf(100000)) < 0) {
            return BigDecimal.valueOf(25000);
        } else {
            return BigDecimal.valueOf(50000);
        }
    }
}

