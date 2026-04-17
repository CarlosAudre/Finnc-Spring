package com.project.FinnC.home;

import com.project.FinnC.container.ContainerService;
import com.project.FinnC.expense.ExpenseService;
import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Month;
import java.util.List;

@RestController
public class HomeController {
    @Autowired
    ContainerService containerService;
    @Autowired
    ExpenseService expenseService;

    @GetMapping("/containers/{year}/{month}")
    public ResponseEntity<List<MostExpensivesContainersDto>> getMostExpensivesContainers(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
            ){
        Month monthEnum = Month.of(month);
        List<MostExpensivesContainersDto> dtos = containerService.getMostExpensivesContainers(year, monthEnum, user);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/expenses/{year}/{month}")
    public ResponseEntity<List<LastExpensesDto>> getLastExpenses(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
    ){
        Month monthEnum = Month.of(month);
        List<LastExpensesDto> dtos = expenseService.getLastExpensesDtos(year, monthEnum, user) ;
        return ResponseEntity.ok(dtos);
    }
}
