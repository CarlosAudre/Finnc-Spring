package com.project.FinnC.container;
import com.project.FinnC.period.Period;
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
@RequestMapping(("/period/{year}/{month}/containers"))
public class ContainerController {
    @Autowired
    ContainerService containerService;

    @Autowired
    PeriodRepository periodRepository;


    @PostMapping
    public ResponseEntity<ContainerDto> createContainer( //To revise
            @RequestBody @Valid ContainerDto containerDTO,
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
            ){
        Month monthEnum = Month.of(month);
        Optional<Period> periodOptional = periodRepository.findByUserAndMonthAndYear(user, monthEnum, year);
        if (periodOptional.isPresent()){
            Period period = periodOptional.get();
            ContainerDto containerDto = containerService.createContainer(containerDTO, user, period);
            return ResponseEntity.ok(containerDto);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContainerDto> updateContainer(
            @RequestBody @Valid ContainerDto containerDto,
            @PathVariable Long id){;
        return ResponseEntity.ok(containerService.updateContainer(containerDto, id));
    }


    @DeleteMapping("{id}/all")
    public ResponseEntity<Void> deleteAllContainerPeriod(@PathVariable Long id){
        containerService.deleteContainer(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<ContainerDto> getContainer(@PathVariable Long id){
        ContainerDto containerDto = containerService.getContainer(id);
        return ResponseEntity.ok(containerDto);
    }
}
