package com.elec5619p5g1.finance_tracker.DTO.Transaction;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateTransactionOptionsDTO {
    List<GeneralInfoDTO> AccountOptions;
    List<GeneralInfoDTO> CurrencyOptions;
    List<GeneralInfoDTO> ExpenseCategoryOptions;
    List<GeneralInfoDTO> IncomeCategoryOptions;
    List<LedgerInfoDTO> LedgerOptions;

    public CreateTransactionOptionsDTO() {
        AccountOptions = new ArrayList<>();
        CurrencyOptions = new ArrayList<>();
        ExpenseCategoryOptions = new ArrayList<>();
        IncomeCategoryOptions = new ArrayList<>();
        LedgerOptions = new ArrayList<>();
    }

    public void addOptions(OptionsType type, String idValue, String displayLabel){
        List list;
        switch (type){
            case Account:
                list = AccountOptions;
                break;
            case Currency:
                list = CurrencyOptions;
                break;
            case ExpenseCategory:
                list = ExpenseCategoryOptions;
                break;
            case IncomeCategory:
                list = IncomeCategoryOptions;
                break;
            case Ledger:
                throw new IllegalArgumentException("Ledger List needs more parameters");
            default:
                throw new IllegalArgumentException("Unknown option type");
        }

        GeneralInfoDTO dto = new GeneralInfoDTO(idValue, displayLabel);
        list.add(dto);
    }

    public void addOptions(OptionsType type, String idValue, String displayLabel, String ledgerType){
        if(type != OptionsType.Ledger){
            throw new IllegalArgumentException("Options other than Ledger should not use this method. ");
        }

        LedgerInfoDTO dto = new LedgerInfoDTO(idValue, displayLabel, ledgerType);
        LedgerOptions.add(dto);
    }

    public enum OptionsType {
        Account,
        Currency,
        ExpenseCategory,
        IncomeCategory,
        Ledger
    }

    @Getter
    @Setter
    class LedgerInfoDTO {
        String value; // Id
        String label; // Name
        String type; // Type

        public LedgerInfoDTO(String value, String label, String type) {
            this.value = value;
            this.label = label;
            this.type = type;
        }
    }

    @Getter
    @Setter
    class GeneralInfoDTO {
        String value;
        String label;

        public GeneralInfoDTO(String value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}
