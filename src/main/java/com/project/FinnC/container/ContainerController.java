package com.project.FinnC.container;
import com.project.FinnC.period.Period;
import com.project.FinnC.period.PeriodBalanceDTO;
import com.project.FinnC.period.PeriodDTO;
import com.project.FinnC.period.PeriodRepository;
import com.project.FinnC.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Month;
import java.util.Optional;

@RestController
@RequestMapping(("/containers"))
public class ContainerController {
    @Autowired
    ContainerService containerService;

    @Autowired
    PeriodRepository periodRepository;


    @PostMapping
    public ResponseEntity createContainer( //To revise
            @RequestBody @Valid ContainerDto containerDTO,
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
            ){
        Month monthEnum = Month.of(month);
        Optional<Period> periodOptional = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year);
        if (periodOptional.isPresent()){
            Period period = periodOptional.get();
            ContainerPeriod containerPeriod = containerService.createContainer(containerDTO, user, period);
            return ResponseEntity.ok(containerPeriod);
        }

        return ResponseEntity.notFound().build();

    }

    @GetMapping("{id}")
    public ResponseEntity getContainer(@PathVariable Long id){
        ContainerDto containerDto = containerService.getContainer(id);
        return ResponseEntity.ok(containerDto);
    }
}
