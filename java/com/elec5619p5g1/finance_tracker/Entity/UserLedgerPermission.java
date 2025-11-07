package com.elec5619p5g1.finance_tracker.Entity;

import com.elec5619p5g1.finance_tracker.Enum.LedgerInvitationStatus;
import com.elec5619p5g1.finance_tracker.Enum.LedgerPermissionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ledger_id"}))
public class UserLedgerPermission {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ledger_id")
    private Ledger ledger;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invited_by", nullable = true)
    private User invitedBy;

    @Enumerated(EnumType.ORDINAL)
    private LedgerPermissionType permissionType;

    @Enumerated(EnumType.ORDINAL)
    private LedgerInvitationStatus invitationStatus;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (invitedAt == null) {
            invitedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}
