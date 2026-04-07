package com.project.FinnC.expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseDto(
        Long id,
        String title,
        BigDecimal value,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
){}


