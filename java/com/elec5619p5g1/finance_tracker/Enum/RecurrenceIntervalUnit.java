package com.elec5619p5g1.finance_tracker.Enum;

public enum RecurrenceIntervalUnit {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    DECADELY("Decadely");

    private String description;

    RecurrenceIntervalUnit(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
