package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monthly expenditure report for Enhanced BarChart
 * Overall budget info and daily expenditure details within a month
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {
    private String reportDate;
    private BigDecimal monthlyFloat;
    private BigDecimal dailyBudget;
    private List<DailyReportDTO> daily;
}
