package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LedgersCollaboratorsItem {
    String avatarUrl;
    String username;
    String permission;
    String invitationStatus;
    boolean manageable;
}
