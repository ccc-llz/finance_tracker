package com.elec5619p5g1.finance_tracker.DTO.Budget;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponseDTO {
    private long ledgerId;
    private BigDecimal monthlyBudget;
    private BigDecimal totalSpent;
    private List<CategoryBudgetDTO> categories;
}
