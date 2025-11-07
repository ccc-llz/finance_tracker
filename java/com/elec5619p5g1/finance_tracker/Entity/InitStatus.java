package com.elec5619p5g1.finance_tracker.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class InitStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private boolean completed;

    private LocalDateTime timestamp;

    public InitStatus() {
    }

    public InitStatus(boolean completed, LocalDateTime timestamp) {
        this.completed = completed;
        this.timestamp = timestamp;
    }
}
