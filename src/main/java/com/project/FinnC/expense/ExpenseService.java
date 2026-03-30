package com.project.FinnC.expense;

import com.project.FinnC.container.Container;
import com.project.FinnC.container.ContainerPeriod;
import com.project.FinnC.container.ContainerPeriodRepository;
import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;
import com.project.FinnC.exeptions.ExpenseContainerNotFoundException;
import com.project.FinnC.exeptions.InsufficientBalanceException;
import com.project.FinnC.period.Period;
import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.period.PeriodService;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @Autowired
    PeriodService periodService;

    @Transactional
    public ExpenseDto createExpense(ExpenseDto expenseDto, Long id) {
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);

        Container container = containerPeriod.getContainer();

        if (expenseDto.endDate().isBefore(container.getStartDate())
                || expenseDto.endDate().isAfter(container.getEndDate())) {
            throw new RuntimeException("Data fora do período do container");
        }

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
            int month = currendDate.getMonthValue();
            int year = currendDate.getYear();
            User user = containerPeriod.getContainer().getUser();

            Period period = periodService.createOrGetPeriod(user, year, month);

            ContainerPeriod cp = containerPeriodRepository.findByContainerAndPeriod(
                            container, period).
                    orElseThrow();

            if (expenseDto.value().compareTo(cp.getEconomy()) > 0) {
                throw new RuntimeException("O valor da despesa é maior que o limite disponível");
            }

            ExpenseContainer expenseContainer = new ExpenseContainer();
            expenseContainer.setContainerPeriod(cp);
            expenseContainer.setExpense(expense);
            expenseContainer.setValue(expenseDto.value());
            expenseContainerRepository.save(expenseContainer);

            BigDecimal totalSpent = expenseContainerRepository.sumByContainerPeriod(cp);

            cp.setTotalSpent(totalSpent);
            cp.setEconomy(cp.getTotalValue().subtract(totalSpent));
            containerPeriodRepository.save(cp);

            BigDecimal totalPeriodSpent = expenseContainerRepository.sumByPeriod(period);
            period.setExpenseTotalSpent(totalPeriodSpent);
            period.setExpenseEconomy(period.getValue().subtract(totalPeriodSpent));
            periodRepository.save(period);

            if (cp.getId().equals(containerPeriod.getId())) {
                currentEc = expenseContainer;
            }

            currendDate = currendDate.plusMonths(1); //one month ahead
        }

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

        Container container = expenseContainer.getContainerPeriod().getContainer();

        // Validate if new date is within container range
        if (expenseDto.endDate().isBefore(container.getStartDate())
                || expenseDto.endDate().isAfter(container.getEndDate())) {
            throw new RuntimeException("Data fora do período do container");
        }

        Expense expense = expenseContainer.getExpense();
        User user = container.getUser();

        BigDecimal oldValue = expenseContainer.getValue();
        BigDecimal newValue = expenseDto.value();

        ContainerPeriod currentCp = expenseContainer.getContainerPeriod();

        // Track all affected entities (always include current)
        Set<ContainerPeriod> affectedCps = new HashSet<>(); //"Set" is a list that doesn't allow repetitions.
        Set<Period> affectedPeriods = new HashSet<>();

        affectedCps.add(currentCp);
        affectedPeriods.add(currentCp.getPeriod());

        // Validate available balance
        BigDecimal available = currentCp.getEconomy().add(oldValue); //"Economy "without the current expense value.
        if (newValue.compareTo(available) > 0) {
            throw new InsufficientBalanceException("O limite do container é inferior ao valor inserido");
        }
        // Update basic expense data
        expense.setTitle(expenseDto.title());
        expense.setEndDate(expenseDto.endDate());

        LocalDate startDate = expense.getStartDate();
        LocalDate endDate = expense.getEndDate();

        List<ExpenseContainer> existingECs =
                expenseContainerRepository.findByExpense(expense);

        // REMOVE invalid months
        for (ExpenseContainer ec : existingECs) {
            if (!isValid(expenseDto, ec)) {
                ContainerPeriod cp = ec.getContainerPeriod();

                expenseContainerRepository.delete(ec);

                affectedCps.add(cp);
                affectedPeriods.add(cp.getPeriod());
            }
        }
        // ADD missing months
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {

            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            Period period = periodService.createOrGetPeriod(user, year, month);

            ContainerPeriod cp = containerPeriodRepository
                    .findByContainerAndPeriod(container, period)
                    .orElseThrow();

            boolean exists = expenseContainerRepository
                    .existsByExpenseAndContainerPeriod(expense, cp);

            if (!exists) {
                ExpenseContainer newEc = new ExpenseContainer();
                newEc.setExpense(expense);
                newEc.setContainerPeriod(cp);
                newEc.setValue(newValue);

                expenseContainerRepository.save(newEc);

                affectedCps.add(cp);
                affectedPeriods.add(period);
            }

            currentDate = currentDate.plusMonths(1);
        }
        // Update current value
        expenseContainer.setValue(newValue);
        expenseContainerRepository.save(expenseContainer);
        // Ensure all changes are flushed before recalculation
        expenseContainerRepository.flush();

        // Recalculate ContainerPeriod
        for (ContainerPeriod cp : affectedCps) {

            BigDecimal totalSpent = expenseContainerRepository
                    .sumByContainerPeriod(cp);

            cp.setTotalSpent(totalSpent);
            cp.setEconomy(cp.getTotalValue().subtract(totalSpent));

            containerPeriodRepository.save(cp);}

        // Recalculate Period
        for (Period period : affectedPeriods) {

            BigDecimal total = expenseContainerRepository
                    .sumByPeriod(period);

            period.setExpenseTotalSpent(total);
            period.setExpenseEconomy(
                    period.getValue().subtract(total)
            );

            periodRepository.save(period);
        }

        expenseRepository.save(expense);

        return new ExpenseDto(
                expenseContainer.getId(),
                expense.getTitle(),
                newValue,
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
        Period period = cp.getPeriod();

        BigDecimal containerPeriodNewTotalSpent = (cp.getTotalSpent().subtract(ec.getValue()));
        BigDecimal containerPeriodNewEconomy = cp.getTotalValue().subtract(containerPeriodNewTotalSpent);

        cp.setTotalSpent(containerPeriodNewTotalSpent);
        cp.setEconomy(containerPeriodNewEconomy);
        containerPeriodRepository.save(cp);

        BigDecimal expensePeriodNewTotalSpent = period.getExpenseTotalSpent().subtract(ec.getValue());
        BigDecimal expensePeriodNewEconomy = period.getValue().subtract(containerPeriodNewTotalSpent);
        period.setExpenseTotalSpent(expensePeriodNewTotalSpent);
        period.setExpenseEconomy(expensePeriodNewEconomy);
        periodRepository.save(period);

        expenseContainerRepository.delete(ec);

        boolean hasMore = expenseContainerRepository
                .existsByExpense(expense);
        if (!hasMore) {
            expenseRepository.delete(expense);
        }
    }
}
