package com.elec5619p5g1.finance_tracker.DTO.Budget;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryBudgetDTO {
    private Long categoryId;
    private String categoryName;
    private BigDecimal budget;
    private BigDecimal spent;
}
