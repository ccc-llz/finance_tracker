package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom period expenditure data for CustomReport
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPeriodReportDTO {
    private String date;
    private String period;
    private BigDecimal spent;
    private BigDecimal ratio;
}
