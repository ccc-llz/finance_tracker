package com.elec5619p5g1.finance_tracker.DTO.Theme;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserThemeDTO {
    long id;
    private String name;
    private String bgImage;
    private String createIcon;
    private String headerColor;
    private String subTitleColor;
    private String itemIcon1;
    private String itemIcon2;
    private String itemIcon3;
    private String itemIcon4;
    private String itemIcon5;
    private String itemIcon6;
}
