package com.elec5619p5g1.finance_tracker.Entity;

import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
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
public class Account {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    private boolean isCard = false;

    private String cardNumber;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private BigDecimal expense;

    @Column(nullable = false)
    private BigDecimal income;

    private boolean isActive = true;

    private CurrencyType currency = CurrencyType.AUD;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy="account", fetch=FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public Account() {}

    public Account(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.balance = BigDecimal.ZERO;
        this.expense = BigDecimal.ZERO;
        this.income = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
