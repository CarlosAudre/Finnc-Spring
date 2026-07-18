package com.project.FinnC.ai;

import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    AiService aiService;

    @PostMapping("/send")
    ResponseEntity<String> getMessage(@RequestBody MessageRequestDTO message,
                                      @AuthenticationPrincipal User user,
                                      @RequestHeader("Authorization") String authorization){
        MessageDTO dto = new MessageDTO(message, user.getName(), user.getId(), authorization);
        String response = aiService.SendMessage(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/period/{year}/{month}")
    ResponseEntity<PeriodContextDTO> getPeriodContext(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
    ){
        return ResponseEntity.ok(aiService.getPeriodContext(year, month, user));
    }

    @GetMapping("/period/{year}")
    ResponseEntity<List<YearPeriodContextDTO>> getYearPeriodContext(
            @AuthenticationPrincipal User user,
            @PathVariable int year
    ){
        return ResponseEntity.ok(aiService.getYearPeriodContext(year, user));
    }

    @GetMapping("/container/{year}/{month}")
    ResponseEntity<List<ContainersContextDTO>> getContainerContext(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
    ){
        return ResponseEntity.ok(aiService.getContainersContext(year, month, user));
    }

    @GetMapping("/container/{year}")
    ResponseEntity<List<ContainersContextDTO>> getYearContainerContext(
            @AuthenticationPrincipal User user,
            @PathVariable int year
    ){
        return ResponseEntity.ok(aiService.getYearContainersContext(year, user));
    }

    @GetMapping("/expense/{year}/{month}")
    ResponseEntity<List<ExpensesContextDTO>> getExpenseContext(
            @AuthenticationPrincipal User user,
            @PathVariable int year,
            @PathVariable int month
    ){
        return ResponseEntity.ok(aiService.getExpensesContext(year, month, user));
    }

    @GetMapping("/expense/{year}")
    ResponseEntity<List<ExpensesContextDTO>> getYearExpenseContext(
            @AuthenticationPrincipal User user,
            @PathVariable int year
    ){
        return ResponseEntity.ok(aiService.getYearExpensesContext(year, user));
    }

}
