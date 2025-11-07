package com.elec5619p5g1.finance_tracker.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.Entity.Achievement;
import com.elec5619p5g1.finance_tracker.Repository.AchievementHistoryRepository;
import com.elec5619p5g1.finance_tracker.Repository.AchievementRepository;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/achievements")
public class AchievementController {
    private final AchievementRepository achievementRepository;
    private final AchievementHistoryRepository achievementHistoryRepository;

    // Get all available achievements
    @GetMapping
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    //Get achievement by ID
    @GetMapping("/{id}")
    public Achievement getAchievementById(@PathVariable Long id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));
    }

    // Get achievement by name
    @GetMapping("/by-name/{name}")
    public Achievement getAchievementByName(@PathVariable String name) {
        return achievementRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Achievement not found"));
    }

    // Get unlocked achievements for a specific user
    @GetMapping("/unlocked-list")
    public List<Achievement> getUnlockedAchievements() {
        long userId = SecurityUtils.getCurrentUserId();
        return achievementHistoryRepository.findAchievementsByUserId(userId);
    }

    // Check if a user has unlocked a specific achievement
    @GetMapping("/check-unlocked/{achievementName}")
    public boolean hasUnlockedAchievement(
            @PathVariable String achievementName
    ) {
        long userId = SecurityUtils.getCurrentUserId();
        return achievementHistoryRepository.existsByUserIdAndAchievementName(userId, achievementName);
    }

    // Get all achievements 
    @GetMapping("/all-list")
    public List<Achievement> getAllAchievementsAdmin() {
        return achievementRepository.findAll(); 
}
}