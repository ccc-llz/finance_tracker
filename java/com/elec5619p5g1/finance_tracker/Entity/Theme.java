package com.elec5619p5g1.finance_tracker.Entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Entity
public class Theme {
    @Id
    @Column(name = "goods_id")
    private Long id;

    @Column(nullable = false)
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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "goods_id",
            foreignKey = @ForeignKey(name = "fk_theme_goods"))
    private Goods goods;
}
