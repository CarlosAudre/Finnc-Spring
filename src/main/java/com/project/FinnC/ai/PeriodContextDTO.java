package com.project.FinnC.ai;

import java.math.BigDecimal;

public record PeriodContextDTO(
        int year,
        int month,
        BigDecimal plannedValue,
        BigDecimal containerSpent,
        BigDecimal expenseSpent,
        BigDecimal remainingAfterExpenses,
        BigDecimal remainingAfterContainers,
        int containerCount
) {
}
