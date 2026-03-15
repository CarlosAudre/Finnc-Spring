package com.project.FinnC.container;

import com.project.FinnC.exeptions.ContainerPeriodNotFoundException;
import com.project.FinnC.exeptions.EmailAlreadyExistsException;
import com.project.FinnC.exeptions.PeriodBalanceInsufficientException;
import com.project.FinnC.period.Period;
import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerService {
    @Autowired
    ContainerRepository containerRepository;
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;
    @Autowired
    PeriodRepository periodRepository;

    @Transactional
    public ContainerDto createContainer(ContainerDto dto, User user, Period currentPeriod) {

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
        containerPeriod.setEconomy(dto.Economy());

        BigDecimal newTotalSpent = currentPeriod.getTotalSpent().add(dto.totalValue());

        if(currentPeriod.getValue().compareTo(newTotalSpent) < 0){
            throw new PeriodBalanceInsufficientException();
        }

        currentPeriod.setTotalSpent(newTotalSpent);
        currentPeriod.setEconomy(currentPeriod.getValue().subtract(newTotalSpent));
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

        Period period = containerPeriod.getPeriod();

        if (dto.totalValue().compareTo(containerPeriod.getTotalSpent()) < 0) {
            throw new RuntimeException("O total gasto do container é maior que o novo valor inserido");
        }

        if (dto.totalValue().compareTo(period.getValue()) > 0) {
            throw new RuntimeException("O saldo do período é menor que o novo valor inserido");
        }

        BigDecimal currentContainerBalance = containerPeriod.getTotalValue();
        BigDecimal newPeriodTotalSpent = (period.getTotalSpent().subtract(currentContainerBalance)).add(dto.totalValue());
        BigDecimal newPeriodEconomy = period.getValue().subtract(newPeriodTotalSpent);

        container.setTitle(dto.title());
        container.setEndDate(dto.endDate());
        container.setColor(dto.color());
        containerPeriod.setTotalValue(dto.totalValue());
        containerPeriod.setEconomy(dto.totalValue().subtract(containerPeriod.getTotalSpent()));
        period.setTotalSpent(newPeriodTotalSpent);
        period.setEconomy(newPeriodEconomy);

        LocalDate newEndDate = dto.endDate();
        //Remove containers where date after new endDate
        if (newEndDate != null) {
            List<ContainerPeriod> containerPeriods =
                    containerPeriodRepository.findByContainer(container);

            List<ContainerPeriod> toDelete = new ArrayList<>(); //<-- All containers where date is after new endDate

            for (ContainerPeriod cp : containerPeriods) {
                Period p = cp.getPeriod();
                LocalDate periodDate = LocalDate.of(p.getYear(), p.getMonth().getValue(), 1); //Transform Year and month to localDate
                if (!cp.getId().equals(containerPeriod.getId()) && //If is different from currentContainerPeriod    
                        periodDate.isAfter(newEndDate)) {
                    toDelete.add(cp);
                }
            }
            containerPeriodRepository.deleteAll(toDelete);
        }

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

    @Transactional
    public void deleteContainer(Long id){
        ContainerPeriod containerPeriod = containerPeriodRepository.findById(id)
                .orElseThrow(ContainerPeriodNotFoundException::new);
        Container container = containerPeriod.getContainer();
        Period period = containerPeriod.getPeriod();

        BigDecimal periodNewTotalSpent = period.getTotalSpent().subtract(containerPeriod.getTotalValue());
        BigDecimal periodNewEconomy = period.getEconomy().add(containerPeriod.getTotalValue());

        period.setTotalSpent(periodNewTotalSpent);
        period.setEconomy(periodNewEconomy);

        containerPeriodRepository.deleteByContainer(container);
        containerRepository.delete(container);
        periodRepository.save(period);
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
