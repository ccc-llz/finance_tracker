package com.elec5619p5g1.finance_tracker.Entity;

import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Enum.RecurrenceIntervalUnit;
import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="Transaction_Templates")
public class TransactionTemplate {
    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;

    @Column(nullable = false)
    private LocalDate startDate;

    private boolean hasEndDate = false;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceIntervalUnit intervalUnit;

    private int recurrenceInterval;

    @Column(nullable = false)
    private LocalDate nextExecutionDate;

    @Column(nullable = false)
    private boolean isActive = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ledger_id")
    private Ledger ledger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public TransactionTemplate() {}

    public TransactionTemplate(User user, String name, BigDecimal amount, Category category, TransactionType type,
                               LocalDate startDate, LocalDate nextExecutionDate,
                               RecurrenceIntervalUnit intervalUnit, int recurrenceInterval, Account account,
                               Ledger ledger, CurrencyType currencyType) {
        this.user = user;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.category = category;
        this.type = type;
        this.intervalUnit = intervalUnit;
        this.recurrenceInterval = recurrenceInterval;
        this.nextExecutionDate = nextExecutionDate;
        this.isActive = true;
        this.ledger = ledger;
        this.currencyType = currencyType;
        this.account = account;
    }

    public TransactionTemplate(User user, String name, BigDecimal amount, Category category, TransactionType type,
                               LocalDate startDate, LocalDate endDate, LocalDate nextExecutionDate,
                               RecurrenceIntervalUnit intervalUnit, int recurrenceInterval, Account account,
                               Ledger ledger, CurrencyType currencyType) {
        this.user = user;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.type = type;
        this.category = category;
        this.endDate = endDate;
        this.hasEndDate = true;
        this.nextExecutionDate = nextExecutionDate;
        this.intervalUnit = intervalUnit;
        this.recurrenceInterval = recurrenceInterval;
        this.isActive = true;
        this.ledger = ledger;
        this.currencyType = currencyType;
        this.account = account;
    }
}
