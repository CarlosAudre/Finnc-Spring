package com.project.FinnC.period;

import com.project.FinnC.container.*;
import com.project.FinnC.exeptions.PeriodBalanceInsufficientException;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service

public class PeriodService {
    @Autowired
    PeriodRepository periodRepository;
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;

    @Transactional
    public Period createOrGetPeriod(User user, int year, int month){
        Month monthEnum = Month.of(month);
        Period period = periodRepository.findByUserAndMonthAndYear(user, monthEnum,year)
                .orElseGet(() -> {
                    Period newPeriod = new Period(monthEnum, year, user);
                    newPeriod.setValue(BigDecimal.ZERO);
                    newPeriod.setTotalSpent(BigDecimal.ZERO);
                    newPeriod.setEconomy(newPeriod.getValue());
                    return periodRepository.save(newPeriod);});

        LocalDate periodDate = LocalDate.of(year, monthEnum, 1);
        List<Container> activeContainers = containerRepository.findActiveContainers(user, periodDate);

            activeContainers.forEach(container -> {
                ContainerPeriod cp = container.getContainerPeriods()
                        .stream().findFirst().orElse(null);
                boolean exists = containerPeriodRepository.existsByContainerAndPeriod(container, period);
                if(!exists){
                    ContainerPeriod newContainerPeriod = getContainerPeriod(container, period, cp);
                    containerPeriodRepository.save(newContainerPeriod);
                }
            });
            periodRepository.save(period);
            return period;
    }

    @Transactional
    private static ContainerPeriod getContainerPeriod(Container container, Period period, ContainerPeriod cp) {
        ContainerPeriod newContainerPeriod = new ContainerPeriod();
        newContainerPeriod.setContainer(container);
        newContainerPeriod.setPeriod(period);
        newContainerPeriod.setTotalValue(cp != null ? cp.getTotalValue() : BigDecimal.ZERO);
        newContainerPeriod.setTotalSpent(cp != null ? cp.getTotalSpent() : BigDecimal.ZERO);
        newContainerPeriod.setEconomy(cp != null ? cp.getEconomy() : BigDecimal.ZERO);

        if(cp != null){
            BigDecimal periodNewTotalSpent = period.getTotalSpent().add(cp.getTotalValue());
            BigDecimal periodNewEconomy = period.getValue().subtract(periodNewTotalSpent);
            int periodNewContainersCount = period.getContainerCount() + 1;
            period.setTotalSpent(periodNewTotalSpent);
            period.setEconomy(periodNewEconomy);
            period.setContainerCount(periodNewContainersCount);
        }

        return newContainerPeriod;
    }

    @Transactional
    public PeriodBalanceDTO saveBalance(PeriodBalanceDTO dto, User user, int year, int month) {
        Month monthEnum = Month.of(month);
        Period period = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year)
                .orElseGet(() -> new Period(dto.value(), monthEnum, year, user));
        if(dto.value().compareTo(period.getTotalSpent()) < 0){
            throw new PeriodBalanceInsufficientException("O total gasto é maior que o novo saldo inserido");
        }
        period.setValue(dto.value());
        period.setEconomy(dto.value().subtract(period.getTotalSpent()));
        periodRepository.save(period);

        return new PeriodBalanceDTO(dto.value());
    }

    public PeriodDto getPeriod(User user, int year, int month){
        Period period = createOrGetPeriod(user, year, month);
        int containerCount = period.getContainerCount();
        List<ContainerDto> containerDtos = containerPeriodRepository.findByPeriod(period)
                .stream().map(containerPeriod -> new ContainerDto(
                        containerPeriod.getId(),
                        containerPeriod.getContainer().getTitle(),
                        containerPeriod.getTotalValue(),
                        containerPeriod.getTotalSpent(),
                        containerPeriod.getEconomy(),
                        containerPeriod.getContainer().getStartDate(),
                        containerPeriod.getContainer().getEndDate(),
                        containerPeriod.getContainer().getColor()
                )).toList();
        return new PeriodDto(year, month, period.getValue(), period.getTotalSpent(),
                period.getEconomy(), containerCount, containerDtos);
    }

}
