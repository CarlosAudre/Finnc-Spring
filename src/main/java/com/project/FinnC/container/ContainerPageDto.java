package com.project.FinnC.container;

import com.project.FinnC.expense.ExpenseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContainerPageDto(
        Long id,
        String title,
        BigDecimal totalValue,
        BigDecimal totalSpent,
        BigDecimal economy,
        LocalDate startDate,
        LocalDate endDate,
        ContainerColor color,
        List<ExpenseDto> expenseDtos

) {
}
