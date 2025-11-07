package com.elec5619p5g1.finance_tracker.DTO.Ledgers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LedgersInvitationListDTO {
    List<Invitation> invitations;

    public LedgersInvitationListDTO() {
        invitations = new ArrayList<>();
    }

    public void addInvitation(long id, String inviter, String ledgerName, String inviteTime, String permission, String status) {
        Invitation invitation = new Invitation(id, inviter, ledgerName, inviteTime, permission, status);
        invitations.add(invitation);
    }

    @Getter
    public class Invitation {
        long id;
        String inviter;
        String ledgerName;
        String inviteTime;
        String permission;
        String status;

        public Invitation(long id, String inviter, String ledgerName, String inviteTime, String permission, String status) {
            this.id = id;
            this.inviter = inviter;
            this.ledgerName = ledgerName;
            this.inviteTime = inviteTime;
            this.permission = permission;
            this.status = status;
        }
    }
}
