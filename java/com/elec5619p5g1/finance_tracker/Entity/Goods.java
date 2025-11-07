package com.elec5619p5g1.finance_tracker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Goods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    String title;
    String description;
    long price;
    String previewImgSrc;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy="goods", fetch= FetchType.LAZY)
    private List<UserGoodsPurchase> userGoodsPurchases = new ArrayList<>();

    @OneToOne(mappedBy = "goods", fetch = FetchType.LAZY, optional = true)
    private Theme theme;

    public Goods(String title, String description, long price, String previewImgSrc) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.previewImgSrc = previewImgSrc;
    }
}
