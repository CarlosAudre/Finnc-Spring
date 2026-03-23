package com.project.FinnC.expense;

import com.project.FinnC.container.Container;
import com.project.FinnC.container.ContainerPeriod;
import com.project.FinnC.container.ContainerPeriodRepository;
import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;
import com.project.FinnC.exeptions.ExpenseContainerNotFoundException;
import com.project.FinnC.exeptions.InsufficientBalance;
import com.project.FinnC.period.Period;
import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    ExpenseContainerRepository expenseContainerRepository;
    @Autowired
    PeriodRepository periodRepository;

    @Transactional
    public ExpenseDto createExpense(ExpenseDto expenseDto, Long id) {
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);

        Expense expense = new Expense();
        expense.setTitle(expenseDto.title());
        expense.setStartDate(expenseDto.startDate());
        expense.setEndDate(expenseDto.endDate());
        expense.setContainer(containerPeriod.getContainer());
        expenseRepository.save(expense);

        LocalDate currendDate = expense.getStartDate();
        LocalDate endDate = expense.getEndDate();

        ExpenseContainer currentEc = null;

        while (!currendDate.isAfter(endDate)) {
            Month month = currendDate.getMonth();
            int year = currendDate.getYear();
            User user = containerPeriod.getContainer().getUser();
            Container container = containerPeriod.getContainer();
            Period period = periodRepository.findByUserAndMonthAndYear(user, month, year)
                    .orElseThrow(ContainerPeriodNotFoundException::new);

            ContainerPeriod cp = containerPeriodRepository.findByContainerAndPeriod(
                            container, period).
                    orElseThrow();

            ExpenseContainer expenseContainer = new ExpenseContainer();
            expenseContainer.setContainerPeriod(cp);
            expenseContainer.setExpense(expense);
            expenseContainer.setValue(expenseDto.value());
            expenseContainerRepository.save(expenseContainer);

            if (cp.getId().equals(containerPeriod.getId())) {
                currentEc = expenseContainer;
            }

            currendDate = currendDate.plusMonths(1); //one month ahead
        }

        if (expenseDto.value().compareTo(containerPeriod.getEconomy()) > 0) {
            throw new RuntimeException("O valor da despesa é maior que o limite disponível");
        }

        BigDecimal newContainerTotalSpent = containerPeriod.getTotalSpent().add(expenseDto.value());
        BigDecimal newContainerEconomy = containerPeriod.getTotalValue().subtract(newContainerTotalSpent);

        containerPeriod.setTotalSpent(newContainerTotalSpent);
        containerPeriod.setEconomy(newContainerEconomy);

        containerPeriodRepository.save(containerPeriod);

        return new ExpenseDto(
                currentEc != null ? currentEc.getId() : null,
                expense.getTitle(),
                expenseDto.value(),
                expense.getStartDate(),
                expense.getEndDate()
        );
    }

    @Transactional
    public ExpenseDto updateExpense(ExpenseDto expenseDto, Long id) {

        ExpenseContainer expenseContainer = expenseContainerRepository.findById(id)
                .orElseThrow(ExpenseContainerNotFoundException::new);

        ContainerPeriod containerPeriod = expenseContainer.getContainerPeriod();
        Container container = containerPeriod.getContainer();
        Expense expense = expenseContainer.getExpense();

        LocalDate oldEndDate = expense.getEndDate();
        LocalDate newEndDate = expenseDto.endDate();

        if (expenseDto.value().compareTo(containerPeriod.getEconomy()) > 0) {
            throw new InsufficientBalance("O limite do container é inferior ao valor inserido");
        }
        BigDecimal oldValue = expenseContainer.getValue();

        expense.setTitle(expenseDto.title());
        expense.setEndDate(newEndDate);
        expenseContainer.setValue(expenseDto.value());

        BigDecimal newTotalSpent =
                containerPeriod.getTotalSpent()
                        .add(expenseDto.value())
                        .subtract(oldValue);

        containerPeriod.setTotalSpent(newTotalSpent);
        containerPeriod.setEconomy(
                containerPeriod.getTotalValue().subtract(newTotalSpent)
        );

        List<ExpenseContainer> expenseContainers =
                expenseContainerRepository.findByExpense(expense);

        for (ExpenseContainer ec : expenseContainers) {
            if (!isValid(expenseDto, ec)) {
                expenseContainerRepository.delete(ec);
            }
        }
        if (newEndDate.isAfter(oldEndDate)) {
            LocalDate currentDate = oldEndDate.plusMonths(1);
            User user = container.getUser();

            while (!currentDate.isAfter(newEndDate)) {
                Month month = currentDate.getMonth();
                int year = currentDate.getYear();

                Period period = periodRepository
                        .findByUserAndMonthAndYear(user, month, year)
                        .orElseThrow(ContainerPeriodNotFoundException::new);

                ContainerPeriod cp = containerPeriodRepository
                        .findByContainerAndPeriod(container, period)
                        .orElseThrow();

                boolean exists = expenseContainerRepository
                        .existsByExpenseAndContainerPeriod(expense, cp);

                if (!exists) {
                    ExpenseContainer newEc = new ExpenseContainer();
                    newEc.setContainerPeriod(cp);
                    newEc.setExpense(expense);
                    newEc.setValue(expenseDto.value());
                    expenseContainerRepository.save(newEc);
                }
                currentDate = currentDate.plusMonths(1);
            }
        }
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
