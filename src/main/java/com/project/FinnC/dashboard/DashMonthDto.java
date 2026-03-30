package com.project.FinnC.dashboard;

import java.math.BigDecimal;
import java.time.Month;

public record DashMonthDto(
        Month month,
        BigDecimal value,
        BigDecimal totalSpent,
        BigDecimal totalEconomy
){}
