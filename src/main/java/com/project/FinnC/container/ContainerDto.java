package com.project.FinnC.container;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

public record  ContainerDto(
        Long id,
        String title,
        BigDecimal totalValue,
        BigDecimal totalSpent,
        BigDecimal Economy,
        LocalDate startDate,
        LocalDate endDate,
        ContainerColor color
) {
}
