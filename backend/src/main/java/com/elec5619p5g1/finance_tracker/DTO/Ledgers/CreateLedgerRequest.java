package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLedgerRequest {
    String name;
    String type;
    String budget;
    String currency;
    String[] invitedUsers;
}
