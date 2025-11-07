package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateTransactionRequest {
    String name;
    String ledger;
    String type;
    String account;
    String amount;
    String currency;
    String category;
    String date;
    String interval;
    String unit;
    boolean isRecurrent;
    boolean hasEndDate;
    String recurStartDate;
    String recurEndDate;
}
