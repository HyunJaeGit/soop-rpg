package com.soop.soop_rpg.service;

import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.StreamerHistory;
import com.soop.soop_rpg.repository.HistoryRepository;
import com.soop.soop_rpg.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PriceScheduler {

    private final StreamerRepository streamerRepository;
    private final HistoryRepository historyRepository;
    private final Random random = new Random();

    // 10초마다 실행 (10000ms)
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void updatePrices() {
        List<Streamer> streamers = streamerRepository.findAll();

        for (Streamer s : streamers) {
            // 1. 시청자 수 랜덤 변동 (-10% ~ +10%)
            int change = (int) (s.getCurrentViewers() * (random.nextDouble() * 0.2 - 0.1));
            int newViewers = Math.max(0, s.getCurrentViewers() + change);
            s.setCurrentViewers(newViewers);

            // 2. 가격 변동 (시청자 수에 비례하게 설정)
            // 예: 시청자 1명당 10G로 계산 (이 로직은 나중에 API 연동 시 정교화)
            int newPrice = 100 + (newViewers / 10);
            s.setCurrentPrice(newPrice);

            // 3. 기록 저장 (History)
            StreamerHistory history = new StreamerHistory();
            history.setStreamerId(s.getId());
            history.setStreamerName(s.getStreamerName());
            history.setViewers(newViewers);
            history.setPrice(newPrice);
            history.setRecordedAt(LocalDateTime.now());
            historyRepository.save(history);
        }
        System.out.println("주가 및 시청자 수 업데이트 완료: " + LocalDateTime.now());
    }
}