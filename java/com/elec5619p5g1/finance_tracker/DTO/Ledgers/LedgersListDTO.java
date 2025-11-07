package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class LedgersListDTO {
    List<LedgerListItem> ledgersList = new ArrayList<>();
    public LedgersListDTO() {}

    public void addItem(long id, String title, BigDecimal amount, String currency, BigDecimal expense, BigDecimal income, boolean isOthersOwned, String owner, boolean isCollaborated, List<String> collaborators, boolean isActive){
        LedgerListItem item = new LedgerListItem(id, title, amount, currency, expense, income, isOthersOwned, owner, isCollaborated, collaborators, isActive);
        ledgersList.add(item);
    }

    @Getter
    class LedgerListItem {
        long id;
        String title;
        BigDecimal amount;
        String currency;
        BigDecimal expense;
        BigDecimal income;
        boolean isOthersOwned;
        String owner;
        boolean isCollaborated;
        List<String> collaborators;
        boolean isActive;

        public LedgerListItem(long id, String title, BigDecimal amount, String currency, BigDecimal expense, BigDecimal income, boolean isOthersOwned, String owner, boolean isCollaborated, List<String> collaborators, boolean isActive) {
            this.id = id;
            this.title = title;
            this.amount = amount;
            this.currency = currency;
            this.expense = expense;
            this.income = income;
            this.isOthersOwned = isOthersOwned;
            this.owner = owner;
            this.isCollaborated = isCollaborated;
            this.collaborators = collaborators;
            this.isActive = isActive;
        }

        public boolean getIsOthersOwned() {
            return isOthersOwned;
        }

        public boolean getIsCollaborated() {
            return isCollaborated;
        }

        public boolean getIsActive() {
            return isActive;
        }
    }
}
