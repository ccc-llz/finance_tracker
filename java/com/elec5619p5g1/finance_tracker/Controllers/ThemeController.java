package com.elec5619p5g1.finance_tracker.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.DTO.Store.StoreGoodsDTO;
import com.elec5619p5g1.finance_tracker.DTO.Theme.UserThemeDTO;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;
import com.elec5619p5g1.finance_tracker.Services.StoreService;
import com.elec5619p5g1.finance_tracker.Services.UserService;

@RestController
@RequestMapping("/api/settings/theme")
public class ThemeController {
    @Autowired
    private StoreService storeService;
    @Autowired
    private UserService userService;

    @GetMapping("/allThemes")
    public ResponseEntity<List<StoreGoodsDTO>> getUserThemes() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            List<StoreGoodsDTO> goodsDTOS = storeService.getUserPurchasedThemes(userId);

            return ResponseEntity.ok(goodsDTOS);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/selectedTheme")
    public ResponseEntity<UserThemeDTO> getUserSelectedTheme() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            UserThemeDTO userThemeDTO = userService.getUserSelectedTheme(userId);

            return ResponseEntity.ok(userThemeDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/setTheme")
    public ResponseEntity<?> setUserSelectedTheme(@RequestParam long themeId) {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            if(themeId != 0 && !storeService.hasPurchased(userId, themeId)) {
                return ResponseEntity.badRequest().body("User hasn't unlock this theme yet");
            }
            UserThemeDTO userThemeDTO = userService.setUserSelectedTheme(userId, themeId);

            return ResponseEntity.ok(userThemeDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
