package com.project.FinnC.ai;

import java.math.BigDecimal;

public record ExpensesContextDTO(
        String title,
        String containerTitle,
        BigDecimal value,
        int month,
        int year
) {}
