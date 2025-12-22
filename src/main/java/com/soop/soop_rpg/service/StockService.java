package com.soop.soop_rpg.service;

import com.soop.soop_rpg.dto.StreamerStockDto;
import com.soop.soop_rpg.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StreamerRepository streamerRepository;

    public List<StreamerStockDto> getTop100Streamers() {
        return streamerRepository.findAll().stream()
                .map(s -> new StreamerStockDto(
                        s.getId(), // [수정] ID 추가
                        s.getStreamerName(),
                        s.getCurrentViewers(),
                        (long) s.getCurrentPrice(),
                        s.getChangeRate(),
                        s.getRankName(),
                        "🌱"
                ))
                .toList();
    }
}