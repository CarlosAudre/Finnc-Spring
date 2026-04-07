package com.project.FinnC.container;

import com.project.FinnC.expense.ExpenseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ContainerPageDto(
        Long id,
        String title,
        BigDecimal totalValue,
        BigDecimal totalSpent,
        BigDecimal economy,
        BigDecimal periodContainerEconomy,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        ContainerColor color,
        List<ExpenseDto> expenseDtos

) {
}
