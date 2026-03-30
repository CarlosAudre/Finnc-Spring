package com.project.FinnC.expense;

import com.project.FinnC.container.ContainerPeriod;
import com.project.FinnC.period.Period;
import com.project.FinnC.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface ExpenseContainerRepository extends JpaRepository<ExpenseContainer, Long> {
    List<ExpenseContainer> findByExpense(Expense expense);
    Boolean existsByExpense(Expense expense);
    Boolean existsByExpenseAndContainerPeriod(Expense expense, ContainerPeriod containerPeriod);

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
}
