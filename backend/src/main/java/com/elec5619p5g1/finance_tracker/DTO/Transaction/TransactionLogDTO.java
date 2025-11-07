package com.elec5619p5g1.finance_tracker.DTO.Transaction;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionLogDTO {

    private long total;

    private List transactions;
}
