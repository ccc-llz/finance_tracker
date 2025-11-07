package com.elec5619p5g1.finance_tracker.DTO.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StoreGoodsDTO {
    long id;
    String title;
    String description;
    String imgurl;
    long price;
    boolean purchased;
}
