package com.laraid.vci.card.cardlimit;

import java.math.BigDecimal;

public record CardLimitResponse(
        BigDecimal recommendedLimit
) {}
