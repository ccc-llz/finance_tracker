package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Weekly expenditure report for Barchart
 * Overall budget info and daily expenditure details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportDTO {
    private String reportDate;
    private BigDecimal weeklyFloat;
    private BigDecimal dailyBudget;
    private List<DailyReportDTO> daily;
}
