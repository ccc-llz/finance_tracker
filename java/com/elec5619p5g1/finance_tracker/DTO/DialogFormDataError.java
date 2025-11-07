package com.elec5619p5g1.finance_tracker.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DialogFormDataError {
    String field;
    String message;

    public DialogFormDataError(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
