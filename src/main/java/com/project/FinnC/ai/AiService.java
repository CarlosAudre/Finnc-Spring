package com.project.FinnC.ai;

import com.project.FinnC.container.Container;
import com.project.FinnC.container.ContainerPeriod;
import com.project.FinnC.container.ContainerPeriodRepository;
import com.project.FinnC.exeptions.PeriodNotFoundException;
import com.project.FinnC.expense.ExpenseContainerRepository;
import com.project.FinnC.period.Period;
import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

import java.time.Month;

@Service
public class AiService {

    //String webhookUrl = "http://localhost:5678/webhook-test/finc-ai"; // Teste Local
    String webhookUrl = "http://localhost:5678/webhook/finc-ai"; //Production url

    @Autowired
    PeriodRepository periodRepository;
    @Autowired
    ContainerPeriodRepository containerPeriodRepository;
    @Autowired
    ExpenseContainerRepository expenseContainerRepository;

    public String SendMessage(MessageDTO dto){
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, dto, String.class);
        return response.getBody();
    }

    public PeriodContextDTO getPeriodContext(int year, int month, User user){
        Month monthEnum = Month.of(month);
        Period period = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year)
                .orElseThrow(PeriodNotFoundException::new);
        return new PeriodContextDTO(
                year,
                month,
                period.getValue(),
                period.getContainerTotalSpent(),
                period.getExpenseTotalSpent(),
                period.getContainerEconomy(),
                period.getExpenseEconomy(),
                period.getContainerCount()

        );
    }

    public List<YearPeriodContextDTO> getYearPeriodContext(int year,  User user){
        return periodRepository.findByYearAndUser(year, user)
                .stream().map(p -> new YearPeriodContextDTO(
                        year,
                        p.getMonth().getValue(),
                        p.getValue(),
                        p.getContainerTotalSpent(),
                        p.getExpenseTotalSpent(),
                        p.getContainerEconomy(),
                        p.getExpenseEconomy(),
                        p.getContainerCount()
                ))
                .toList();

    }

    public List<ContainersContextDTO> getContainersContext(int year, int month, User user){
        Month monthEnum = Month.of(month);
        Period period = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year)
                .orElseThrow(PeriodNotFoundException::new);
        return containerPeriodRepository.findByPeriod(period)
                .stream().map(cp -> new ContainersContextDTO(
                    cp.getContainer().getTitle(),
                    cp.getContainer().getColor(),
                    cp.getTotalValue(),
                    cp.getTotalSpent(),
                    cp.getEconomy(),
                        cp.getPeriod().getMonth().getValue()
                ))
                .toList();
    }

    public List<ContainersContextDTO> getYearContainersContext(int year, User user){
        return containerPeriodRepository.findByPeriodYearAndPeriodUser(year, user)
                .stream().map(cp -> new ContainersContextDTO(
                        cp.getContainer().getTitle(),
                        cp.getContainer().getColor(),
                        cp.getTotalValue(),
                        cp.getTotalSpent(),
                        cp.getEconomy(),
                        cp.getPeriod().getMonth().getValue()
                ))
                .toList();
    }


    public List<ExpensesContextDTO> getExpensesContext(int year, int month, User user) {
        Month monthEnum = Month.of(month);
        Period period = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year)
                .orElseThrow(PeriodNotFoundException::new);
        return expenseContainerRepository.findByPeriod(period)
                .stream().map(ec -> new ExpensesContextDTO(
                        ec.getExpense().getTitle(),
                        ec.getExpense().getContainer().getTitle(),
                        ec.getValue(),
                        ec.getContainerPeriod().getPeriod().getMonth().getValue(),
                        ec.getContainerPeriod().getPeriod().getYear()
                ))
                .toList();
    }

    public List<ExpensesContextDTO> getYearExpensesContext(int year, User user){
        return expenseContainerRepository.findExpenseByPeriodYearAndUser(year, user)
                .stream().map(ec -> new ExpensesContextDTO(
                        ec.getExpense().getTitle(),
                        ec.getExpense().getContainer().getTitle(),
                        ec.getValue(),
                        ec.getContainerPeriod().getPeriod().getMonth().getValue(),
                        ec.getContainerPeriod().getPeriod().getYear()
                ))
                .toList();
    }
}
