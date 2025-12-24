package com.soop.soop_rpg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * [클래스 역할]: 주식 종목이 되는 스트리머의 정보를 담는 DB 테이블 모델입니다.
 */
@Entity // 이 클래스를 바탕으로 DB에 'Streamer'라는 이름의 테이블을 만듭니다.
@Getter // 데이터를 가져오는 메서드(getStreamerName 등)를 자동으로 생성합니다.
@Setter // 데이터를 저장하는 메서드(setStreamerName 등)를 자동으로 생성합니다.
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동으로 만듭니다. (JPA 필수)
public class Streamer {

    @Id // 이 필드를 테이블의 'PK(기본키)'로 지정합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB에서 자동으로 번호를 매겨주는 고유 번호입니다.

    private String streamerName;  // 스트리머의 닉네임
    private String rankName;      // 현재 등급 (예: 골드, 실버 등)
    private int currentPrice;     // 현재 주가 (G 단위)
    private double changeRate;    // 변동률 (전날 대비 몇 % 오르고 내렸는지)
    private int currentViewers;   // 실시간 시청자 수 (SOOP API에서 가져올 핵심 데이터)

    /**
     * [생성자 역할]: 새로운 스트리머 데이터를 처음 만들 때 사용합니다.
     */
    public Streamer(String streamerName, String rankName, int currentPrice, double changeRate, int currentViewers) {
        this.streamerName = streamerName;
        this.rankName = rankName;
        this.currentPrice = currentPrice;
        this.changeRate = changeRate;
        this.currentViewers = currentViewers;
    }
}