package com.project.FinnC.home;

import com.project.FinnC.container.ContainerColor;

import java.math.BigDecimal;

public record MostExpensivesContainersDto(
        Long id,
        String title,
        BigDecimal totalSpent,
        BigDecimal totalValue,
        ContainerColor color
) {

}
