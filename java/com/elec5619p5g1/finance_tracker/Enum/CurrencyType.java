package com.elec5619p5g1.finance_tracker.Enum;

public enum CurrencyType {
    AUD("Australia Dollars", "au"),
    CNY("Chinese Yuan", "cn"),
    USD("United States Dollars", "us");

    private String description;
    private String shortCode;

    CurrencyType(String description, String shortCode) {
        this.description = description;
        this.shortCode = shortCode;
    }

    public String getDescription() {
        return description;
    }

    public String getShortCode() {
        return shortCode;
    }
}
