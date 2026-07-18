package com.project.FinnC.expense;

import com.project.FinnC.container.ContainerPeriod;
import com.project.FinnC.home.LastExpensesDto;
import com.project.FinnC.period.Period;
import com.project.FinnC.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;
import java.util.Optional;


@Repository
public interface ExpenseContainerRepository extends JpaRepository<ExpenseContainer, Long> {
    List<ExpenseContainer> findByExpense(Expense expense);
    Boolean existsByExpense(Expense expense);
    Boolean existsByExpenseAndContainerPeriod(Expense expense, ContainerPeriod containerPeriod);

    @Query("""
    SELECT ec
    FROM ExpenseContainer ec
    JOIN ec.containerPeriod cp
    WHERE cp.period = :period
""")
    List<ExpenseContainer> findByPeriod(Period period);

    @Query("""
    SELECT ec
    FROM ExpenseContainer ec
    JOIN ec.containerPeriod cp
    JOIN cp.period p
    WHERE p.year = :year    
    AND p.user = :user    
""")
    List<ExpenseContainer> findExpenseByPeriodYearAndUser(int year, User user);

    @Query("""
    SELECT COALESCE(SUM(ec.value), 0)
    FROM ExpenseContainer ec
    WHERE ec.containerPeriod = :cp
""")
    BigDecimal sumByContainerPeriod(@Param("cp") ContainerPeriod cp);

    @Query("""
    SELECT COALESCE(SUM(ec.value), 0)
    FROM ExpenseContainer ec
    WHERE ec.containerPeriod.period = :period
""")
    BigDecimal sumByPeriod(@Param("period") Period period);

    @Query("""
            SELECT NEW com.project.FinnC.home.LastExpensesDto
            (cp.id, e.title, c.title, c.color, ec.value)
            FROM ExpenseContainer ec
            JOIN ec.expense e
            JOIN ec.containerPeriod cp
            JOIN cp.container c
            WHERE cp.period.year = :year
            AND cp.period.month = :month
            AND c.user.id = :userId
            ORDER BY e.createdAt DESC
            """)
    List<LastExpensesDto> findLastExpenses(int year, Month month, Long userId, Pageable pageable);
}
