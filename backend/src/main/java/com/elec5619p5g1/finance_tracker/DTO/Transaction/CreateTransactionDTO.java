package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateTransactionDTO {
    long userId;
    long transactionId;
    long ledgerId;
    String name;
    String type;
    String category;
    LocalDate recordDate;
    LocalDateTime timestamp;

    public CreateTransactionDTO() {}

    public CreateTransactionDTO(long userId, long transactionId, long ledgerId, String name, String type, String category, LocalDate recordDate, LocalDateTime timestamp) {
        this.userId = userId;
        this.transactionId = transactionId;
        this.ledgerId = ledgerId;
        this.name = name;
        this.type = type;
        this.category = category;
        this.recordDate = recordDate;
        this.timestamp = timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(long ledgerId) {
        this.ledgerId = ledgerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
