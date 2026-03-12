package com.project.FinnC.period;
import com.project.FinnC.container.ContainerDto;


import java.math.BigDecimal;
import java.util.List;

public record PeriodDTO(
        int year,
        int month,
        BigDecimal value,
        BigDecimal totalSpent,
        BigDecimal economy,
        int containerCount,
        List<ContainerDto> containerDtos
) {
}
