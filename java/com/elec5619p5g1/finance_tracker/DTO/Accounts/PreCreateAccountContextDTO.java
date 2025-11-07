package com.elec5619p5g1.finance_tracker.DTO.Accounts;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PreCreateAccountContextDTO {
    List<Info> currencies = new ArrayList<>();

    public PreCreateAccountContextDTO() {}

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
