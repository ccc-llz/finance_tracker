package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Daily expenditure data for BarChartCard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDTO {
    private String date;
    private String day;
    private BigDecimal spent;
    private BigDecimal ratio;
}
