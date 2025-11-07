package com.elec5619p5g1.finance_tracker.Config;

import com.elec5619p5g1.finance_tracker.Entity.Category;
import com.elec5619p5g1.finance_tracker.Entity.InitStatus;
import com.elec5619p5g1.finance_tracker.Enum.TransactionType;
import com.elec5619p5g1.finance_tracker.Repository.InitStatusRepository;
import com.elec5619p5g1.finance_tracker.Services.CategoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Log
@RequiredArgsConstructor
public class DefaultDataInitializer {
    private final InitStatusRepository initStatusRepository;
    private final CategoryService categoryService;

    @PostConstruct
    public void init() {
        if (!isInitialised()) {
            for (String category : categoryService.expenseDefaultCategories) {
                Category newCategory = categoryService.findOrCreateByNameAndType(category, TransactionType.EXPENSE);
                categoryService.setCategoryType(newCategory, TransactionType.EXPENSE);

            }

            for (String category : categoryService.incomeDefaultCategories) {
                Category newCategory = categoryService.findOrCreateByNameAndType(category,  TransactionType.INCOME);
                categoryService.setCategoryType(newCategory, TransactionType.INCOME);
            }

            InitStatus initStatus = new InitStatus(true, LocalDateTime.now());
            initStatusRepository.save(initStatus);

            log.info("Init status saved successfully");
            return;
        }

        log.info("Init done before. Skip successfully!");
    }

    private boolean isInitialised() {
        return initStatusRepository.count() != 0;
    }
}
