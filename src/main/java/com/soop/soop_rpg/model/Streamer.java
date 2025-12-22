package com.soop.soop_rpg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity // 이 클래스를 DB 테이블로 쓰겠다는 선언
@Getter @Setter
@NoArgsConstructor
public class Streamer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB용 고유 번호

    private String streamerName;
    private String rankName;
    private int currentPrice;
    private double changeRate;
    private int currentViewers;

    // 생성자 (테스트 데이터 생성용)
    public Streamer(String streamerName, String rankName, int currentPrice, double changeRate, int currentViewers) {
        this.streamerName = streamerName;
        this.rankName = rankName;
        this.currentPrice = currentPrice;
        this.changeRate = changeRate;
        this.currentViewers = currentViewers;
    }
}