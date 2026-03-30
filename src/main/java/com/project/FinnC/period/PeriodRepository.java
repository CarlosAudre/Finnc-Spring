package com.project.FinnC.period;

import com.project.FinnC.dashboard.DashMonthDto;
import com.project.FinnC.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Optional;
import java.util.List;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
    Optional<Period> findByUserAndMonthAndYear(User user, Month month, int year);

    @Query("""
            SELECT COALESCE(SUM(p.value), 0)
            FROM Period p
            WHERE p.year = :year
            AND p.user.id = :userId
            """)
    BigDecimal sumTotalValueByYear(int year, Long userId);

    @Query("""
            SELECT COALESCE(SUM(p.expenseTotalSpent), 0)
            FROM Period p
            WHERE p.year = :year
            AND p.user.id = :userId
            """)
    BigDecimal sumTotalSpentByYear(int year, Long userId);

    @Query("""
            SELECT COALESCE(SUM(p.expenseEconomy), 0)
            FROM Period p
            WHERE p.year = :year
            AND p.user.id = :userId
            """)
    BigDecimal sumTotalEconomyByYear(int year, Long userId);

    @Query("""
            SELECT COALESCE(SUM(p.expenseTotalSpent), 0)
            FROM Period p
            WHERE p.year = :year
            AND p.month = :month
            AND p.user.id = :userId
            """)
    BigDecimal sumTotalSpentByMonth(int year, Month month, Long userId);

    @Query("""
            SELECT NEW com.project.FinnC.dashboard.DashMonthDto
            (p.month, p.value, p.expenseTotalSpent, p.expenseEconomy)
            FROM Period p
            WHERE p.year = :year
            AND p.user = :user
            ORDER BY p.month
            """)
    List<DashMonthDto> findDashboardByYear(User user, int year);

}


