package com.elec5619p5g1.finance_tracker.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elec5619p5g1.finance_tracker.DTO.DialogFormDataError;
import com.elec5619p5g1.finance_tracker.DTO.Ops.NewStoreGoodsRequest;
import com.elec5619p5g1.finance_tracker.DTO.Store.StoreGoodsDTO;
import com.elec5619p5g1.finance_tracker.Entity.Goods;
import com.elec5619p5g1.finance_tracker.Services.FileService;
import com.elec5619p5g1.finance_tracker.Services.StoreService;
import com.elec5619p5g1.finance_tracker.seed.SeedDataGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/ops")
@Slf4j
@RequiredArgsConstructor
public class OpsController {
    private final StoreService storeService;
    private final FileService fileService;
    private final SeedDataGenerator seedDataGenerator;

    @PostMapping("/newGoods")
    public ResponseEntity<?> uploadNewGoods(@RequestPart("info") NewStoreGoodsRequest request, @RequestPart("img") MultipartFile img) {
        try {
            if (request.getPrice() < 0) {
                DialogFormDataError error = new DialogFormDataError("price", "Price must be no less than zero. ");
                return ResponseEntity.badRequest().body(error);
            }

            String filePath = fileService.saveFile(img, "goods");
            Goods goods = storeService.addGoods(request.getTitle(), request.getDescription(), filePath, request.getPrice());
            return ResponseEntity.ok(goods);
        } catch (Exception e) {
            log.error("{},\n {}", e.getMessage(), e.getStackTrace());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/allGoods")
    public ResponseEntity<?> getAllGoods() {
        try {
            List<Goods> goods = storeService.getAllGoods();
            List<StoreGoodsDTO> dtos = new ArrayList<>();
            for (Goods g : goods) {
                StoreGoodsDTO dto = new StoreGoodsDTO(g.getId(), g.getTitle(), g.getDescription(), g.getPreviewImgSrc(), g.getPrice(), false);
                dtos.add(dto);
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("{},\n {}", e.getMessage(), e.getStackTrace());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/disableGoods")
    public ResponseEntity<?> disableGoods(@RequestParam long goodsId) {
        try {
            boolean result = storeService.removeGoods(goodsId);
            if (result) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Goods disabled successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to disable goods"
                ));
            }
        } catch (Exception e) {
            log.error("Error disabling goods: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error occurred while disabling goods: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/seed-data")
    public ResponseEntity<?> generateSeedData() {
        try {
            seedDataGenerator.generateSeedData();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Seed data generated successfully",
                "demoUser", Map.of(
                    "username", "demo",
                    "password", "demo123"
                )
            ));
        } catch (IllegalStateException e) {
            log.warn("Seed data generation skipped: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error generating seed data", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Failed to generate seed data: " + e.getMessage()
            ));
        }
    }
}
