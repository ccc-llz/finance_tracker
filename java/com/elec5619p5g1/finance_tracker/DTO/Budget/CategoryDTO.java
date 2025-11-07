package com.elec5619p5g1.finance_tracker.DTO.Budget;

import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class CategoryDTO {
    private Long id;
    private String name;
    private TransactionType transactionType;

    public CategoryDTO(Long id, String name, TransactionType transactionType) {
        this.id = id;
        this.name = name;
        this.transactionType = transactionType;

    }
}