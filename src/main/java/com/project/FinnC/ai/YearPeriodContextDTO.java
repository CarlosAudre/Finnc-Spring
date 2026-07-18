package com.project.FinnC.ai;

import java.math.BigDecimal;

public record YearPeriodContextDTO(
        int year,
        int month,
        BigDecimal plannedValue,
        BigDecimal containerSpent,
        BigDecimal expenseSpent,
        BigDecimal remainingAfterContainers,
        BigDecimal remainingAfterExpenses,
        int containerCount
) {
}
