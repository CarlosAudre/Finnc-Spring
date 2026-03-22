package com.project.FinnC.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ExpenseContainerRepository extends JpaRepository<ExpenseContainer, Long> {
    List<ExpenseContainer> findByExpense(Expense expense);
    Boolean existsByExpense(Expense expense);
}
