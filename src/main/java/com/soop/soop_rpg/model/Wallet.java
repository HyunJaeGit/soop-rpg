package com.soop.soop_rpg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balance;   // 현재 보유 자산 (Gold)
    private String userRank; // 유저 등급 (예: 새싹 투자자)

    public Wallet(Long balance, String userRank) {
        this.balance = balance;
        this.userRank = userRank;
    }
}