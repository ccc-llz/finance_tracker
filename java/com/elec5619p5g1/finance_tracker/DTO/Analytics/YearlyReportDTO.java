package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Yearly expenditure report for Enhanced BarChart
 * Overall budget info and monthly expenditure details within a year
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyReportDTO {
    private String reportDate;
    private BigDecimal yearlyFloat;
    private BigDecimal monthlyBudget;
    private List<MonthlyDataDTO> monthly;
}
