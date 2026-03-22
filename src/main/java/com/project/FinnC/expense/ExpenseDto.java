package com.project.FinnC.expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDto(
        Long id,
        String title,
        BigDecimal value,
        LocalDate startDate,
        LocalDate endDate
){}


