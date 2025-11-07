package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLedgerResponse {
    long ledgerId;
    String name;
    String type;
    long ownerId;

    public CreateLedgerResponse(long ledgerId, String name, String type, long ownerId) {
        this.ledgerId = ledgerId;
        this.name = name;
        this.type = type;
        this.ownerId = ownerId;
    }
}
