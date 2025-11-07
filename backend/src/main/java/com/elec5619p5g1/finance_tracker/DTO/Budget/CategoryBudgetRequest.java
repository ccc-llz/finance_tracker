package com.elec5619p5g1.finance_tracker.DTO.Budget;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryBudgetRequest {

    private long ledgerId;
    private long categoryId;
    private String budget;

    public BigDecimal toBigDecimalBudget() {
        try {
            return new BigDecimal(this.budget);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid budget format: " + this.budget);
        }
    }
}
