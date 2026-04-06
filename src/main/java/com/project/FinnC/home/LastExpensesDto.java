package com.project.FinnC.home;

import com.project.FinnC.container.ContainerColor;

import java.math.BigDecimal;

public record LastExpensesDto(
        Long id,
        String title,
        String containerTitle,
        ContainerColor color,
        BigDecimal value
) {
}
