package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.StreamerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<StreamerHistory, Long> {
    List<StreamerHistory> findTop20ByStreamerIdOrderByRecordedAtDesc(Long streamerId);

    // 오래된 데이터 삭제용 (선택 사항)
    void deleteByRecordedAtBefore(LocalDateTime dateTime);
}