package com.elec5619p5g1.finance_tracker.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class AchievementHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "achievement_name", referencedColumnName = "name"),
        @JoinColumn(name = "achievement_id", referencedColumnName = "id")
    })
    private Achievement achievement;

    private LocalDateTime achievedAt = LocalDateTime.now();
}
