package com.elec5619p5g1.finance_tracker.DTO.Ops;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewStoreGoodsRequest {
    String title;
    String description;
    int price;
}
