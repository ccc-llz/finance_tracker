package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreateRecurrentTransactionDTO {
    long userId;
    long templateId;
    long ledgerId;
    String name;
    String type;
    String category;
    LocalDateTime timestamp;
    LocalDate firstRecordDate;
    LocalDate nextExecutionDate;

    public CreateRecurrentTransactionDTO() {}

    public CreateRecurrentTransactionDTO(long userId, long templateId, long ledgerId, String name, String type, String category, LocalDateTime timestamp, LocalDate firstRecordDate, LocalDate nextExecutionDate) {
        this.userId = userId;
        this.templateId = templateId;
        this.ledgerId = ledgerId;
        this.name = name;
        this.type = type;
        this.category = category;
        this.timestamp = timestamp;
        this.firstRecordDate = firstRecordDate;
        this.nextExecutionDate = nextExecutionDate;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDate getFirstRecordDate() {
        return firstRecordDate;
    }

    public void setFirstRecordDate(LocalDate firstRecordDate) {
        this.firstRecordDate = firstRecordDate;
    }

    public LocalDate getNextExecutionDate() {
        return nextExecutionDate;
    }

    public void setNextExecutionDate(LocalDate nextExecutionDate) {
        this.nextExecutionDate = nextExecutionDate;
    }
}
