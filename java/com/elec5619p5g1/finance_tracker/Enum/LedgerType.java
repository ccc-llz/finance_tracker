package com.elec5619p5g1.finance_tracker.Enum;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum LedgerType {
    PERSONAL("personal"),
    COLLAB("collab");

    String code;

    LedgerType(String code) {
        this.code = code;
    }
}
