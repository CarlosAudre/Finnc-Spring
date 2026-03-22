package com.project.FinnC.expense;

import com.project.FinnC.container.Container;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByContainer(Container container);

    @Query("""
            SELECT e FROM Expense e
            WHERE e.container = :container
            AND e.startDate <= :periodDate
            AND (e.endDate >= :periodDate OR e.endDate IS NULL)
    """)
    List<Expense> findActiveExpenses(Container container, LocalDate periodDate); //PeriodDate must stay between e.startDate and e.EndDate
    //Expense has a endDate?
}
