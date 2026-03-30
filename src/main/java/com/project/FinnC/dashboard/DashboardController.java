package com.project.FinnC.dashboard;

import com.project.FinnC.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard/{year}")
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/{month}")
    public ResponseEntity<SummaryDto> getSummary(@PathVariable int year,
                                                 @PathVariable int month,
                                                 @AuthenticationPrincipal User user){
       SummaryDto summaryDto = dashboardService.getSummary(year, month, user);
       return ResponseEntity.ok(summaryDto);
    }

    @GetMapping("/overview")
    public ResponseEntity<List<DashMonthDto>> getOverviewChartDate(@AuthenticationPrincipal User user,
                                                                   @PathVariable int year){
        List<DashMonthDto> dashMonthDtos = dashboardService.getOverviewChartDate(user, year);
        return ResponseEntity.ok(dashMonthDtos);
    }
}
