package com.elec5619p5g1.finance_tracker.Entity;

import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Enum.LedgerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Ledger {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private BigDecimal expense;

    @Column(nullable = false)
    private BigDecimal income;

    @Column(nullable = false)
    private BigDecimal budget;

    @Column(nullable = false)
    private boolean isArchived;

    @Column(nullable = false)
    private LedgerType ledgerType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private CurrencyType defaultCurrency = CurrencyType.AUD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id", nullable = false)
    User owner;

    @OneToMany(mappedBy="ledger", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy="ledger", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<UserLedgerPermission> userPermissions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Ledger() {}

    public Ledger(String name, LedgerType ledgerType, User owner) {
        this.name = name;
        this.ledgerType = ledgerType;
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
        this.budget = BigDecimal.ZERO;
        this.expense = BigDecimal.ZERO;
        this.income = BigDecimal.ZERO;
        this.isArchived = false;
        this.isActive = true;
    }
}
