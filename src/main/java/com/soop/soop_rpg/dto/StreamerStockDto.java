package com.soop.soop_rpg.dto;

/**
 * 스트리머 주식 정보를 담는 데이터 객체 (Record)
 * 필드가 총 6개인지 꼭 확인하세요!
 */
public record StreamerStockDto(
        String streamerName,    // 1. 이름
        int currentViewers,     // 2. 시청자 수
        long currentPrice,      // 3. 현재 가격
        double changeRate,      // 4. 변동률 (이게 추가되어야 합니다!)
        String rankName,        // 5. 등급 이름
        String imagePath        // 6. 이미지 경로
) {}