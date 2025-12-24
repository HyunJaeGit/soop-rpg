package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.Streamer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * [역할]: DB에서 스트리머 정보를 꺼내오거나 저장하는 창구입니다.
 */
public interface StreamerRepository extends JpaRepository<Streamer, Long> {

    /**
     * [메서드 역할]: 스트리머의 이름을 기반으로 정보를 찾습니다.
     * Optional을 쓰면 "정보가 없을 수도 있음"을 안전하게 처리할 수 있습니다.
     */
    Optional<Streamer> findByStreamerName(String streamerName);
}