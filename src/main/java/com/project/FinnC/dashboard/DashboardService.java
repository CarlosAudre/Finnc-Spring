package com.project.FinnC.dashboard;

import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    @Autowired
    PeriodRepository periodRepository;

    public SummaryDto getSummary(int year, int month, User user){
        BigDecimal totalReceived = periodRepository.sumTotalValueByYear(year, user.getId());
        BigDecimal totalSpent = periodRepository.sumTotalSpentByYear(year, user.getId());
        BigDecimal totalEconomy = periodRepository.sumTotalEconomyByYear(year, user.getId());
        BigDecimal trendPercentage = trendPercentage(year, month, user);

        return new SummaryDto(
                totalReceived,
                totalSpent,
                totalEconomy,
                trendPercentage
        );
    }

    private BigDecimal trendPercentage(int year, int month, User user){
        int previousMonth = (month - 1);
        if(previousMonth < 1){
            return BigDecimal.ZERO;
        }
        Month currentMonthEnum = Month.of(month);
        Month previousMonthEnum = Month.of(previousMonth);
        BigDecimal current = periodRepository.sumTotalSpentByMonth(year, currentMonthEnum, user.getId());
        BigDecimal previous = periodRepository.sumTotalSpentByMonth(year, previousMonthEnum, user.getId());

        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    public List<DashMonthDto> getOverviewChartDate(User user, int year) {
        List<DashMonthDto> result = periodRepository.findDashboardByYear(user, year);

        Map<Month, DashMonthDto> map = result.stream()
                .collect(Collectors.toMap(DashMonthDto::month, Function.identity())); //function.identity() -> take the entire dto
                                                                                      //DashMonthDTO::month -> take month as key
        List<DashMonthDto> finalList = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            Month month = Month.of(i);
            finalList.add(map.getOrDefault(month, //i = key //if dto value is null, i create a default (BigDecimal.ZERO)
                    new DashMonthDto(
                            month,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    )
            ));
        }
        return finalList;
    }
}
