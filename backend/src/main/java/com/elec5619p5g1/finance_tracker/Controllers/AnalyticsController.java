package com.elec5619p5g1.finance_tracker.Controllers;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.DTO.Analytics.CustomReportDTO;
import com.elec5619p5g1.finance_tracker.DTO.Analytics.MonthlyReportDTO;
import com.elec5619p5g1.finance_tracker.DTO.Analytics.WeeklyIncomeReportDTO;
import com.elec5619p5g1.finance_tracker.DTO.Analytics.WeeklyReportDTO;
import com.elec5619p5g1.finance_tracker.DTO.Analytics.YearlyReportDTO;
import com.elec5619p5g1.finance_tracker.Services.AnalyticsService;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/barchart/{ledgerId}")
    public WeeklyReportDTO getWeeklyReport(@PathVariable Long ledgerId) {
        return analyticsService.getWeeklyExpenseTrend(ledgerId);
    }

    @GetMapping("/spendtrend/{ledgerId}")
    public WeeklyIncomeReportDTO getWeeklyIncomeTrend(@PathVariable Long ledgerId){
        return analyticsService.getWeeklyIncomeTrend(ledgerId);
    }

    @GetMapping("/monthly/{ledgerId}")
    public MonthlyReportDTO getMonthlyReport(
            @PathVariable Long ledgerId,
            @RequestParam int year,
            @RequestParam int month) {
        return analyticsService.getMonthlyExpenseTrend(ledgerId, year, month);
    }

    @GetMapping("/yearly/{ledgerId}")
    public YearlyReportDTO getYearlyReport(
            @PathVariable Long ledgerId,
            @RequestParam int year) {
        return analyticsService.getYearlyExpenseTrend(ledgerId, year);
    }

    @GetMapping("/custom/{ledgerId}")
    public CustomReportDTO getCustomRangeReport(
            @PathVariable Long ledgerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return analyticsService.getCustomRangeExpenseTrend(ledgerId, start, end);
    }
}
