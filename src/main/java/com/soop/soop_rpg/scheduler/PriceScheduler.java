package com.soop.soop_rpg.scheduler;

import com.soop.soop_rpg.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * [클래스 역할]: 주기적으로 외부 데이터를 가져와서 게임 내 시세를 업데이트합니다.
 */
@Component
@RequiredArgsConstructor
public class PriceScheduler {

    private final ApiService apiService;
    // stockService가 여기서 사용되지 않는다면 제거하거나, 나중에 필요할 때 다시 추가합니다.

    /**
     * [메서드 역할]: 1분마다 SOOP 상위 10명 스트리머 데이터를 동기화합니다.
     */
    @Scheduled(fixedRate = 60000) // 60,000ms = 1분
    public void updateMarket() {
        System.out.println("=== [스케줄러] 실시간 데이터 동기화 시작 ===");

        // ApiService에서 이름을 바꾼 메서드를 정확히 호출합니다.
        apiService.syncTopStreamerData();

        System.out.println("=== [스케줄러] 동기화 완료 ===");
    }
}