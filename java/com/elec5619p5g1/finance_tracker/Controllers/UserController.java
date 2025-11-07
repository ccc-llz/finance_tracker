package com.elec5619p5g1.finance_tracker.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.DTO.Budget.CategoryDTO;
import com.elec5619p5g1.finance_tracker.Entity.Category;
import com.elec5619p5g1.finance_tracker.Entity.User;
import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;
import com.elec5619p5g1.finance_tracker.Services.CategoryService;
import com.elec5619p5g1.finance_tracker.Services.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/{userId}/categories")
    public List<CategoryDTO> getUserCategories(
            @PathVariable Long userId,
            @RequestParam(required = false) TransactionType type) {

        List<Category> categories = userService.getUserAccessibleCategories(userId);

        if (type != null) {
            categories = categories.stream()
                    .filter(c -> c.getTransactionType() == type)
                    .collect(Collectors.toList());
        }

        return categories.stream()
                .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getTransactionType()))
                .collect(Collectors.toList());
    }

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> request) {
        try {
            long userId = SecurityUtils.getCurrentUserId();
            String newEmail = request.get("email");
            
            if (newEmail == null || newEmail.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email cannot be empty"));
            }
            
            User updatedUser = userService.updateUserEmail(userId, newEmail.trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", updatedUser.getEmail());
            response.put("message", "Email updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update email"));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            long userId = SecurityUtils.getCurrentUserId();
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Current password cannot be empty"));
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "New password cannot be empty"));
            }
            
            userService.changeUserPassword(userId, currentPassword.trim(), newPassword.trim());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to change password"));
        }
    }

}
