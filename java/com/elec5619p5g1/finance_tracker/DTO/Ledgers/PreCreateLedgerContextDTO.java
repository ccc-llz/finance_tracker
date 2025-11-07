package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PreCreateLedgerContextDTO {
    List<Info> currencies = new ArrayList<>();

    public PreCreateLedgerContextDTO() {}

    public void addInfo(String value, String label) {
        currencies.add(new Info(value, label));
    }

    @Setter
    @Getter
    class Info {
        String value;
        String label;

        public Info(String value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}
