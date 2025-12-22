package com.soop.soop_rpg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streamerName; // 어떤 스트리머의 주식인가?
    private int quantity;        // 몇 주를 가졌는가?
    private long averagePrice;   // 평단가 (얼마에 샀는가?)

    public Portfolio(String streamerName, int quantity, long averagePrice) {
        this.streamerName = streamerName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }
}