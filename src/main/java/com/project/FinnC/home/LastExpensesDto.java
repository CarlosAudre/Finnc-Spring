package com.project.FinnC.home;

import java.math.BigDecimal;

public record LastExpensesDto(
        Long id,
        String title,
        String containerTitle,
        BigDecimal value
) {
}
