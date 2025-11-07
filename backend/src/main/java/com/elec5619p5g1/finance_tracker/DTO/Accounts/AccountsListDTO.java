package com.elec5619p5g1.finance_tracker.DTO.Accounts;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AccountsListDTO {
    List<AccountsListDTO.AccountListItem> accountsList = new ArrayList<>();
    public AccountsListDTO() {}

    public void addItem(long id, String title, BigDecimal amount, String currency, BigDecimal expense, BigDecimal income, boolean isCard, String cardNumber, boolean isActive){
        AccountsListDTO.AccountListItem item = new AccountsListDTO.AccountListItem(id, title, amount, currency, expense, income, isCard, cardNumber, isActive);
        accountsList.add(item);
    }

    @Getter
    class AccountListItem {
        long id;
        String title;
        BigDecimal amount;
        String currency;
        BigDecimal expense;
        BigDecimal income;
        boolean isCard;
        String cardNumber;
        boolean isActive;

        public AccountListItem(long id, String title, BigDecimal amount, String currency, BigDecimal expense, BigDecimal income, boolean isCard, String cardNumber, boolean isActive) {
            this.id = id;
            this.title = title;
            this.amount = amount;
            this.currency = currency;
            this.expense = expense;
            this.income = income;
            this.isCard = isCard;
            this.cardNumber = cardNumber;
            this.isActive = isActive;
        }

        public boolean getIsActive() {
            return isActive;
        }
    }
}
