package com.project.FinnC.period;

import com.project.FinnC.user.User;
import com.project.FinnC.user.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Optional;

@RestController
@RequestMapping("/period")
public class PeriodController {
    @Autowired
    PeriodRepository periodRepository;

    @PostMapping("/{year}/{month}/balance")
    public ResponseEntity fillBalance(
            @RequestBody @Valid PeriodDTO dto,
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
            ) {
        Month monthEnum = Month.of(month);
        Optional<Period> period = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year);
        if (period.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Period newPeriod = new Period(dto.value(), monthEnum, year, user);
        periodRepository.save(newPeriod);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{year}/{month}/balance")
    public ResponseEntity getBalance(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
    ){
        Month monthEnum = Month.of(month);
        Optional<Period> balance = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year);
        if (balance.isEmpty()) {
            return ResponseEntity.ok(new PeriodBalanceDTO(BigDecimal.ZERO));
        }
        Period periodBalance = balance.get();
        
        PeriodBalanceDTO periodBalanceDto = new PeriodBalanceDTO(periodBalance.getValue() );
        return ResponseEntity.ok(periodBalance);

    }


}
