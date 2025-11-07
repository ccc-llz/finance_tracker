package com.elec5619p5g1.finance_tracker.DTO.Analytics;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyIncomeReportDTO {
    private String date;
    private String day;
    private BigDecimal income;
}
