package com.project.FinnC.period;

import com.project.FinnC.container.*;
import com.project.FinnC.exeptions.PeriodBalanceInsufficientException;
import com.project.FinnC.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Container> activeContainers = containerRepository.findAllByUser(user);
        activeContainers.stream().filter( container ->
                !container.getStartDate().isAfter(periodDate) && (container.getEndDate() == null ||
                        (!container.getEndDate().isBefore(periodDate)
                        )
                ))
                .forEach(container -> {
                    boolean exists = containerPeriodRepository.existsByContainerAndPeriod(container, period);
                    if(!exists){
                        ContainerPeriod containerPeriod = new ContainerPeriod();
                        containerPeriod.setContainer(container);
                        containerPeriod.setPeriod(period);
                        containerPeriod.setTotalValue(BigDecimal.ZERO);
                        containerPeriod.setTotalSpent(BigDecimal.ZERO);
                        containerPeriod.setEconomy(BigDecimal.ZERO);

                        containerPeriodRepository.save(containerPeriod);
                    }
                });
        return period;
    }


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
