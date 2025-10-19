package com.laraid.vci.card.cardlimit;

import com.laraid.vci.enums.GovernmentIdType;
import java.math.BigDecimal;

public record CardLimitRequest(
        BigDecimal monthlySalary,
        GovernmentIdType governmentIdType,
        String governmentId
) {}
