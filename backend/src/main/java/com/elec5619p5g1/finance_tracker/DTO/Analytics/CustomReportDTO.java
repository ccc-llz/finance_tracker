package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom range expenditure report for Enhanced BarChart
 * Overall budget info and period-based expenditure details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomReportDTO {
    private String reportDate;
    private BigDecimal customFloat;
    private BigDecimal dailyBudget;
    private List<CustomPeriodReportDTO> daily;
}
