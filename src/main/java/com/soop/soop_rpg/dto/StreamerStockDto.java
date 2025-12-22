package com.soop.soop_rpg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 스트리머 주식 정보를 담는 데이터 객체 (Record)
 * 필드가 총 7개인지 꼭 확인하세요!
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamerStockDto{
        private Long id;                // [추가] 스트리머 고유 ID
        private String streamerName;    // 1. 이름
        private int currentViewers;     // 2. 시청자 수
        private long currentPrice;      // 3. 현재 가격
        private double changeRate;      // 4. 변동률 (이게 추가되어야 합니다!)
        private String rankName;        // 5. 등급 이름
        private String imagePath;       // 6. 이미지 경로
}