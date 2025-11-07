package com.elec5619p5g1.finance_tracker.DTO.Store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class StoreInfoDTO {
    List<StoreGoodsDTO> storeGoods = new ArrayList<>();
    long tokenBalance;
}
