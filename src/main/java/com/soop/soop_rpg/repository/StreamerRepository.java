package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.Streamer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamerRepository extends JpaRepository<Streamer, Long> {
    // JpaRepository를 상속받으면 save, findAll 등을 자동으로 사용할 수 있습니다.

}
