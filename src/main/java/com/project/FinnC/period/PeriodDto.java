package com.project.FinnC.period;
import com.project.FinnC.container.ContainerDto;


import java.math.BigDecimal;
import java.util.List;

public record PeriodDto(
        int year,
        int month,
        BigDecimal value,
        BigDecimal containerTotalSpent,
        BigDecimal expenseTotalSpent,
        BigDecimal containerEconomy,
        BigDecimal expenseEconomy,
        int containerCount,
        List<ContainerDto> containerDtos
) {
}
