package com.elec5619p5g1.finance_tracker.DTO.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class StorePurchaseDTO {
    long id;
    long tokenBalance;
    boolean purchased;
}
