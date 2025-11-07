package com.elec5619p5g1.finance_tracker.Enum;

public enum TransactionType {
    INCOME("Income"),
    EXPENSE("Expense");

    private String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
