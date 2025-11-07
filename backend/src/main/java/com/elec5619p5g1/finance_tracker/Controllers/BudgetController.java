package com.elec5619p5g1.finance_tracker.Controllers;

import com.elec5619p5g1.finance_tracker.DTO.Budget.CategoryBudgetRequest;
import com.elec5619p5g1.finance_tracker.DTO.Budget.BudgetResponseDTO;
import com.elec5619p5g1.finance_tracker.DTO.Budget.CategoryBudgetDTO;
import com.elec5619p5g1.finance_tracker.Entity.Ledger;
import com.elec5619p5g1.finance_tracker.Services.BudgetService;
import com.elec5619p5g1.finance_tracker.Services.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
@Slf4j
public class BudgetController {

    private final BudgetService budgetService;
    private final LedgerService ledgerService;

    @GetMapping("/{ledgerId}")
    public BudgetResponseDTO getBudget(@PathVariable Long ledgerId) {
        return budgetService.getBudget(ledgerId);
    }

    @PutMapping("/{ledgerId}")
    public ResponseEntity<?> updateBudget(@PathVariable long ledgerId,
                                          @RequestBody BigDecimal newBudget) {
        budgetService.updateBudget(ledgerId, newBudget);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{ledgerId}/category")
    public ResponseEntity<List<CategoryBudgetDTO>> getCategoryBudgets(@PathVariable long ledgerId){
        return ResponseEntity.ok(budgetService.getCategoryBudgets(ledgerId));
    }

    @PostMapping("/{ledgerId}/categories/{categoryId}")
    public ResponseEntity<?> setCategoryBudget(
            @RequestBody CategoryBudgetRequest request) {
        long ledgerId = request.getLedgerId();
        long categoryId = request.getCategoryId();
        BigDecimal newBudget = request.toBigDecimalBudget();
        budgetService.setCategoryBudget(ledgerId, categoryId, newBudget);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{ledgerId}/categories/{categoryId}")
    public ResponseEntity<?> deleteCategoryBudget(@PathVariable long ledgerId,
                                                  @PathVariable long categoryId) {
        budgetService.deleteCategoryBudget(ledgerId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
