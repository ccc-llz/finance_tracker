package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.Data;

@Data
public class LedgerCollaboratorInvitationUpdateRequest {
    String operation; // REVOKE, RETRY, SET
    Integer permission; // 0 - READONLY, 1 - EDITABLE
}
