package com.elec5619p5g1.finance_tracker.DTO.Analytics;


import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyIncomeReportDTO {
    private String reportDate;
    private BigDecimal totalIncome;
    private List<DailyIncomeReportDTO> daily;
}
