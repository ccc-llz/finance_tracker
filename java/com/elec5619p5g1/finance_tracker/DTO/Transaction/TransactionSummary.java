package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import java.math.BigDecimal;

public class TransactionSummary {
    private String category;
    private BigDecimal totalAmount;

    public TransactionSummary(String category, BigDecimal totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
