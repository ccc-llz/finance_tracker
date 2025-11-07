package com.elec5619p5g1.finance_tracker.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elec5619p5g1.finance_tracker.DTO.Store.StoreGoodsDTO;
import com.elec5619p5g1.finance_tracker.DTO.Store.StoreInfoDTO;
import com.elec5619p5g1.finance_tracker.DTO.Store.StorePurchaseDTO;
import com.elec5619p5g1.finance_tracker.Security.SecurityUtils;
import com.elec5619p5g1.finance_tracker.Services.StoreService;
import com.elec5619p5g1.finance_tracker.Services.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<StoreInfoDTO> getStoreGoods() {
        long userId = SecurityUtils.getCurrentUserId();
        try {
            List<StoreGoodsDTO> goodsDTOS = storeService.getUserStoreGoodsList(userId, storeService.getAllGoodsOldToNew());
            long tokenBalance = userService.getUserTokenBalance(userId);
            StoreInfoDTO storeInfoDTO = new StoreInfoDTO(goodsDTOS, tokenBalance);

            return ResponseEntity.ok(storeInfoDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/purchase/{goodsId}")
    public ResponseEntity<?> getPurchaseGoods(@PathVariable long goodsId) {
        long userId = SecurityUtils.getCurrentUserId();
        if(storeService.hasPurchased(userId, goodsId)) {
            return ResponseEntity.badRequest().body("User has already purchased this item");
        }

        try {
            boolean purchased = storeService.purchaseGoods(userId, goodsId);
            if (!purchased) {
                return ResponseEntity.badRequest().body("Failed to purchase: insufficient tokens or other error");
            }
            long newTokenBalance = userService.getUserTokenBalance(userId);
            StorePurchaseDTO dto = new StorePurchaseDTO(goodsId, newTokenBalance, purchased);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
}
