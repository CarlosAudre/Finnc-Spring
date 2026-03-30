package com.project.FinnC.dashboard;

import java.math.BigDecimal;

public record SummaryDto(
        BigDecimal totalReceived,
        BigDecimal totalSpent,
        BigDecimal totalEconomy,
        BigDecimal trendPercentage
) {
}
