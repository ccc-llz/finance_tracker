package com.elec5619p5g1.finance_tracker.DTO.Authenticate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String userId;
    private String username;
    private String email;
    private String userRole;

    public RegisterResponse(String userId, String username, String email, String userRole) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.userRole = userRole;
    }
}
