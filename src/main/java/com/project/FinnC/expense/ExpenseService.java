package com.project.FinnC.expense;

import com.project.FinnC.container.ContainerPeriod;
import com.project.FinnC.container.ContainerPeriodRepository;
import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;
import com.project.FinnC.exeptions.ExpenseContainerNotFoundException;
import com.project.FinnC.exeptions.InsufficientBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    ExpenseContainerRepository expenseContainerRepository;

    @Transactional
    public ExpenseDto createExpense(ExpenseDto expenseDto, Long id) {
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);

        Expense expense = new Expense();
        expense.setTitle(expenseDto.title());
        expense.setStartDate(expenseDto.startDate());
        expense.setEndDate(expenseDto.endDate());
        expense.setContainer(containerPeriod.getContainer());

        ExpenseContainer expenseContainer = new ExpenseContainer();
        expenseContainer.setValue(expenseDto.value());
        expenseContainer.setContainerPeriod(containerPeriod);
        expenseContainer.setExpense(expense);

        if (expenseDto.value().compareTo(containerPeriod.getEconomy()) > 0) {
            throw new RuntimeException("O valor da despesa é maior que o limite disponível");
        }

        BigDecimal newContainerTotalSpent = containerPeriod.getTotalSpent().add(expenseDto.value());
        BigDecimal newContainerEconomy = containerPeriod.getTotalValue().subtract(newContainerTotalSpent);

        containerPeriod.setTotalSpent(newContainerTotalSpent);
        containerPeriod.setEconomy(newContainerEconomy);

        expenseRepository.save(expense);
        expenseContainerRepository.save(expenseContainer);
        containerPeriodRepository.save(containerPeriod);

        return new ExpenseDto(
                expenseContainer.getId(),
                expense.getTitle(),
                expenseContainer.getValue(),
                expense.getStartDate(),
                expense.getEndDate()
        );
    }

    @Transactional
    public ExpenseDto updateExpense(ExpenseDto expenseDto, Long id) {

        ExpenseContainer expenseContainer = expenseContainerRepository.findById(id)
                .orElseThrow(ExpenseContainerNotFoundException::new);
        System.out.println("EC: " + expenseContainer);

        ContainerPeriod containerPeriod = expenseContainer.getContainerPeriod();

        if (expenseDto.value().compareTo(containerPeriod.getEconomy()) > 0) {
            throw new InsufficientBalance("O limite do container é inferior ao valor inserido");
        }
        BigDecimal containerPeriodNewTotalSpent =
                (containerPeriod.getTotalSpent()
                        .add(expenseDto.value()))
                        .subtract(expenseContainer.getValue());
        BigDecimal containerPeriodNewEconomy = containerPeriod.getTotalValue().subtract(containerPeriodNewTotalSpent);

        Expense expense = expenseContainer.getExpense();
        expenseContainer.setValue(expenseDto.value());
        expense.setTitle(expenseDto.title());
        expense.setEndDate(expenseDto.endDate());

        containerPeriod.setTotalSpent(containerPeriodNewTotalSpent);
        containerPeriod.setEconomy(containerPeriodNewEconomy);

        List<ExpenseContainer> expenseContainers =
                expenseContainerRepository.findByExpense(expense);

        for (ExpenseContainer ec : expenseContainers) {
            boolean isValid = isValid(expenseDto, ec);

            if (!isValid) {
                expenseContainerRepository.delete(ec);
            }
        }

        expenseContainerRepository.save(expenseContainer);
        expenseRepository.save(expense);
        containerPeriodRepository.save(containerPeriod);

        return new ExpenseDto(
                expenseContainer.getId(),
                expense.getTitle(),
                expenseContainer.getValue(),
                expense.getStartDate(),
                expense.getEndDate()
        );
    }

    private static boolean isValid(ExpenseDto expenseDto, ExpenseContainer ec) {
        int periodYear = ec.getContainerPeriod().getPeriod().getYear();
        Month periodMonth = ec.getContainerPeriod().getPeriod().getMonth();

        LocalDate periodDate = LocalDate.of(
                periodYear,
                periodMonth,
                1 //day
        );
        LocalDate expenseStartDate = ec.getExpense().getStartDate();
        LocalDate newExpenseEndDate = expenseDto.endDate();

        return !expenseStartDate.isAfter(periodDate) && //<=
                (newExpenseEndDate == null || !newExpenseEndDate.isBefore(periodDate)); //>=
    }

    @Transactional
    public void deleteExpense(Long id) {
        ExpenseContainer ec = expenseContainerRepository.findById(id)
                .orElseThrow(ExpenseContainerNotFoundException::new);

        Expense expense = ec.getExpense();

        ContainerPeriod cp = ec.getContainerPeriod();

        BigDecimal containerPeriodNewTotalSpent = (cp.getTotalSpent().subtract(ec.getValue()));
        BigDecimal containerPeriodNewEconomy = cp.getTotalValue().subtract(containerPeriodNewTotalSpent);

        cp.setTotalSpent(containerPeriodNewTotalSpent);
        cp.setEconomy(containerPeriodNewEconomy);
        expenseContainerRepository.delete(ec);
        containerPeriodRepository.save(cp);

        boolean hasMore = expenseContainerRepository
                .existsByExpense(expense);
        if (!hasMore) {
            expenseRepository.delete(expense);

        }
    }
}
