package com.project.FinnC.ai;

import com.project.FinnC.container.ContainerColor;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContainersContextDTO(
        String title,
        ContainerColor color,
        BigDecimal totalValue,
        BigDecimal totalSpent,
        BigDecimal economy,
        int month
) {
}
