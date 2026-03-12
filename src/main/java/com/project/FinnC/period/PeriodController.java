package com.project.FinnC.period;

import com.project.FinnC.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/period")
public class PeriodController {
    @Autowired
    PeriodRepository periodRepository;
    @Autowired
    PeriodService periodService;


    @GetMapping("/{year}/{month}")
    public ResponseEntity getPeriod(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
    ){
        PeriodDTO period = periodService.getPeriod(user, year, month);
        return ResponseEntity.ok(period);

    }

    @PutMapping("/{year}/{month}/balance") //PutMapping is sufficient because it will never create another balance in the same period, only modify it
    public ResponseEntity fillBalance(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PeriodBalanceDTO periodDTO,
            @PathVariable int year,
            @PathVariable int month
    ){
        periodService.saveBalance(periodDTO, user, year, month);
        return ResponseEntity.ok().build();
    }



}
