package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Monthly expenditure data for YearlyReport
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDataDTO {
    private String month;
    private BigDecimal spent;
    private BigDecimal ratio;
}
