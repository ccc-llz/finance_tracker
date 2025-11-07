package com.elec5619p5g1.finance_tracker.Entity;

import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name="Transactions")
public class Transaction {
    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.ORDINAL)
    private TransactionType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CurrencyType currencyType;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="template_id")
    private TransactionTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ledger_id")
    private Ledger ledger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    private Category category;

    public Transaction() {}

    public Transaction(TransactionType type, String name, BigDecimal amount, CurrencyType currencyType, LocalDate transactionDate, User user, Ledger ledger, Category category, Account account) {
        this.type = type;
        this.name = name;
        this.amount = amount;
        this.currencyType = currencyType;
        this.transactionDate = transactionDate;
        this.user = user;
        this.ledger = ledger;
        this.category = category;
        this.account = account;
    }
}
