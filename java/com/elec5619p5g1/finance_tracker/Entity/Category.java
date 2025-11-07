package com.elec5619p5g1.finance_tracker.Entity;

import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Categories",uniqueConstraints = @UniqueConstraint(columnNames = {"name", "type"}))
public class Category {
    @Id
    @GeneratedValue
    long id;

    @Column(nullable = false)
    String name;

    boolean isActive = true;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="type")
    TransactionType transactionType;

    @OneToMany(mappedBy="category", fetch=FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy="category", fetch=FetchType.LAZY)
    private List<UserCategoryAvailability> userAvailabilities = new ArrayList<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
    }
}
