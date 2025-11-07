package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TransactionLogRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer selectedAccountId;

    private Integer currentLedgerId;
}
