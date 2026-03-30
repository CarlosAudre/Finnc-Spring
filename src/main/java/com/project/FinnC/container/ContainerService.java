package com.project.FinnC.container;

import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;

import com.project.FinnC.exeptions.InsufficientBalanceException;
import com.project.FinnC.expense.*;
import com.project.FinnC.period.Period;
import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ContainerService {
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;
    @Autowired
    PeriodRepository periodRepository;
    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    ExpenseContainerRepository expenseContainerRepository;

    @Transactional
    public ContainerDto createContainer(ContainerDto dto, User user, Month month, int year) {

        if (dto.endDate().isBefore(dto.startDate())){
            throw new RuntimeException("A data de fim não pode ser antes da data de inicio");
        }

        Period currentPeriod = periodRepository.findByUserAndMonthAndYear(user,month, year)
                .orElseThrow(() -> new RuntimeException("Period not found"));

        Container container = new Container();
        container.setUser(user);
        container.setTitle(dto.title());
        container.setStartDate(dto.startDate());
        container.setEndDate(dto.endDate());
        container.setColor(dto.color());

        ContainerPeriod containerPeriod = new ContainerPeriod();
        containerPeriod.setContainer(container);
        containerPeriod.setPeriod(currentPeriod);
        containerPeriod.setTotalValue(dto.totalValue());
        containerPeriod.setTotalSpent(BigDecimal.ZERO);
        containerPeriod.setEconomy(dto.totalValue());

        BigDecimal newTotalSpent = currentPeriod.getContainerTotalSpent().add(dto.totalValue());

        if(currentPeriod.getValue().compareTo(newTotalSpent) < 0){
            throw new InsufficientBalanceException();
        }

        currentPeriod.setContainerTotalSpent(newTotalSpent);
        currentPeriod.setContainerEconomy(currentPeriod.getValue().subtract(newTotalSpent));
        currentPeriod.setContainerCount(currentPeriod.getContainerCount() + 1);

        containerRepository.save(container);
        containerPeriodRepository.save(containerPeriod);

        return new ContainerDto(
                containerPeriod.getId(),
                container.getTitle(),
                containerPeriod.getTotalValue(),
                containerPeriod.getTotalSpent(),
                containerPeriod.getEconomy(),
                container.getStartDate(),
                container.getEndDate(),
                container.getColor()
        );
    }

    public ContainerDto updateContainer(ContainerDto dto, Long id){
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);

        Container container = containerPeriod.getContainer();

        if(dto.endDate().isBefore(container.getStartDate())){
            throw new RuntimeException(("A data de fim não pode ser anterior à data de início."));
        }

        Period period = containerPeriod.getPeriod();

        BigDecimal newPeriodTotalSpent = getBigDecimal(dto, containerPeriod, period);
        BigDecimal newPeriodEconomy = period.getValue().subtract(newPeriodTotalSpent);

        List<Expense> expenses = container.getExpenses();
        for(Expense expense : expenses){
            if (expense.getEndDate().isAfter(dto.endDate())){
                throw new RuntimeException("Redução de data inválida: existe despesa conflitante.");
            }
        }
        container.setTitle(dto.title());
        container.setEndDate(dto.endDate());
        container.setColor(dto.color());
        containerPeriod.setTotalValue(dto.totalValue());
        containerPeriod.setEconomy(dto.totalValue().subtract(containerPeriod.getTotalSpent()));
        period.setContainerTotalSpent(newPeriodTotalSpent);
        period.setContainerEconomy(newPeriodEconomy);

        LocalDate newEndDate = dto.endDate();
        //Remove containers where date is after new endDate
        List<ContainerPeriod> containerPeriods =
                containerPeriodRepository.findByContainer(container);

        List<ContainerPeriod> toDelete = new ArrayList<>(); //<-- All containers where date is after new endDate

        for (ContainerPeriod cp : containerPeriods) {
            Period p = cp.getPeriod();
            LocalDate periodDate = LocalDate.of(p.getYear(), p.getMonth().getValue(), 1); //Transform Year and month to localDate
            if (!cp.getId().equals(containerPeriod.getId()) && //If is different from currentContainerPeriod
                    periodDate.isAfter(newEndDate)) {
                toDelete.add(cp);
                p.setContainerTotalSpent(p.getContainerTotalSpent().subtract(cp.getTotalValue()));
                p.setContainerEconomy(p.getValue().subtract(p.getContainerTotalSpent()));
                p.setContainerCount(p.getContainerCount() - 1);
                periodRepository.save(p);
            }
        }
        containerPeriodRepository.deleteAll(toDelete);

        containerPeriodRepository.save(containerPeriod);
        containerRepository.save(container);
        periodRepository.save(period);

        return new ContainerDto(
                containerPeriod.getId(),
                container.getTitle(),
                containerPeriod.getTotalValue(),
                containerPeriod.getTotalSpent(),
                containerPeriod.getEconomy(),
                container.getStartDate(),
                container.getEndDate(),
                container.getColor()
        );
    }

    private static BigDecimal getBigDecimal(ContainerDto dto, ContainerPeriod containerPeriod, Period period) {
        if (dto.totalValue().compareTo(containerPeriod.getTotalSpent()) < 0) {
            throw new RuntimeException("O total gasto do container é maior que o novo valor inserido");
        }

        if (dto.totalValue().compareTo(period.getValue()) > 0) {
            throw new RuntimeException("O saldo do período é menor que o novo valor inserido");
        }

        BigDecimal currentContainerBalance = containerPeriod.getTotalValue();
        return (period.getContainerTotalSpent().subtract(currentContainerBalance)).add(dto.totalValue());
    }

    @Transactional
    public void deleteContainer(Long id){
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);
        Container container = containerPeriod.getContainer();

        List<ContainerPeriod> containerPeriods = containerPeriodRepository.findByContainer(container);

        for (ContainerPeriod cp : containerPeriods) {
            Period period = cp.getPeriod();

            BigDecimal expenseValueToRemove =
                    expenseContainerRepository.sumByContainerPeriod(cp);

            BigDecimal containerValueToRemove = containerPeriodRepository.sumContainerByContainerAndPeriod(container, period);

            period.setExpenseTotalSpent(
                    period.getExpenseTotalSpent().subtract(expenseValueToRemove)
            );

            period.setContainerTotalSpent(
                    period.getContainerTotalSpent().subtract(containerValueToRemove)
            );

            period.setExpenseEconomy(
                    period.getValue().subtract(period.getExpenseTotalSpent())
            );

            period.setContainerEconomy(
                    period.getValue().subtract(period.getContainerTotalSpent())
            );

            period.setContainerCount(period.getContainerCount() - 1);

            periodRepository.save(period);
        }

        containerPeriodRepository.deleteAll(containerPeriods);
        containerRepository.delete(container);
    }
    

    public ContainerPageDto getContainer(Long id){
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);

        Period period = containerPeriod.getPeriod();
        LocalDate periodDate = LocalDate.of(period.getYear(), period.getMonth(), 1);

        return processContainer(containerPeriod, periodDate);
    }

    private ContainerPageDto processContainer(ContainerPeriod containerPeriod, LocalDate periodDate) {

        Container container = containerPeriod.getContainer();

        List<Expense> expenses = expenseRepository.findActiveExpenses(container, periodDate);

        List<ExpenseDto> expenseDtos = expenses.stream()
                .map(expense -> {
                    ExpenseContainer ec = expense.getExpenseContainers()
                            .stream()
                            .filter(exc -> exc.getContainerPeriod().getId()
                                    .equals(containerPeriod.getId())) // At this point, I filter the ExpenseContainers where the containerPeriod matches the current containerPeriod. There is actually just one, but I need to get it as a single element.
                            .findFirst()// Now I get the first element from the filtered list, which I will use to fill the value in the ExpenseDto.
                            .orElse(null);
                    if (ec == null) return null;
                    return new ExpenseDto(
                            ec.getId(),
                            expense.getTitle(),
                            ec.getValue(),
                            expense.getStartDate(),
                            expense.getEndDate()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new ContainerPageDto(
                containerPeriod.getId(),
                container.getTitle(),
                containerPeriod.getTotalValue(),
                containerPeriod.getTotalSpent(),
                containerPeriod.getEconomy(),
                container.getStartDate(),
                container.getEndDate(),
                container.getColor(),
                expenseDtos
        );
    }
}