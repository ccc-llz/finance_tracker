package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionLogVO {
    BigDecimal getAmount();
    LocalDate getTransactionDate();
    String getName();

    CategoryView getCategory();
    interface CategoryView {
        String getName();
    }
//    String getNote();
}
