package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    // 이미 산 적이 있는 주식인지 확인하기 위해 이름을 기준으로 찾기 추가
    Optional<Portfolio> findByStreamerName(String streamerName);
}