package com.project.FinnC.expense;

import com.project.FinnC.container.ContainerPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ExpenseContainerRepository extends JpaRepository<ExpenseContainer, Long> {
    List<ExpenseContainer> findByExpense(Expense expense);
    Boolean existsByExpense(Expense expense);
    Boolean existsByExpenseAndContainerPeriod(Expense expense, ContainerPeriod containerPeriod);
}
