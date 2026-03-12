package com.project.FinnC.container;

import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;
import com.project.FinnC.exeptions.EmailAlreadyExistsException;
import com.project.FinnC.exeptions.PeriodBalanceInsufficientException;
import com.project.FinnC.period.Period;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ContainerService {
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;

    @Transactional
    public ContainerPeriod createContainer(ContainerDto dto, User user, Period currentPeriod){
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
        containerPeriod.setTotalSpent(dto.totalSpent());
        containerPeriod.setEconomy(dto.Economy());
        containerPeriod.setTotalSpent(BigDecimal.ZERO);

        BigDecimal newTotalSpent = currentPeriod.getTotalSpent().add(dto.totalValue());

        if(currentPeriod.getValue().compareTo(newTotalSpent) < 0){
            throw new PeriodBalanceInsufficientException();
        }

        currentPeriod.setTotalSpent(newTotalSpent);
        currentPeriod.setEconomy(currentPeriod.getValue().subtract(newTotalSpent));
        int containerCount = currentPeriod.getContainerCount();
        currentPeriod.setContainerCount(containerCount + 1);

        containerRepository.save(container);
        containerPeriodRepository.save(containerPeriod);

        return containerPeriod;
    }

    public ContainerDto getContainer(Long id){
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);
        return new ContainerDto(
                containerPeriod.getId(),
                containerPeriod.getContainer().getTitle(),
                containerPeriod.getTotalValue(),
                containerPeriod.getTotalSpent(),
                containerPeriod.getEconomy(),
                containerPeriod.getContainer().getStartDate(),
                containerPeriod.getContainer().getEndDate(),
                containerPeriod.getContainer().getColor()
        );
    }
}
