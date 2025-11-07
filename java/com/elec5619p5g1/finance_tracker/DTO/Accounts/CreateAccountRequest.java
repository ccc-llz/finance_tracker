package com.elec5619p5g1.finance_tracker.DTO.Accounts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateAccountRequest {
    String name;
    String balance;
    String currency;
    @JsonProperty("isCard")
    boolean isCard;
    String cardNumber;
}
