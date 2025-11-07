package com.elec5619p5g1.finance_tracker.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.elec5619p5g1.finance_tracker.Enum.CurrencyType;
import com.elec5619p5g1.finance_tracker.Enum.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue
    private long id;

    @Column(unique=true, nullable=false)
    private String username;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    private CurrencyType defaultCurrencyType = CurrencyType.AUD;

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private Role role = Role.USER;
    private long tokenBalance = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isActive = false;

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<TransactionTemplate> transactionTemplates = new ArrayList<>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Ledger> ownedLedgers = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<UserLedgerPermission> userLedgerPermissions = new ArrayList<>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<UserCategoryAvailability> categoryAvailabilities = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<UserGoodsPurchase>  userGoodsPurchases = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_theme_goods_id", foreignKey = @ForeignKey(name="fk_user_selected_theme"))
    private Theme selectedTheme;

    public User() {}

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
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
