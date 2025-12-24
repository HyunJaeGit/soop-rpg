package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.StreamerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * [클래스 역할]: 스트리머의 기록들을 DB에서 조회하는 도구입니다.
 */
@Repository
public interface StreamerHistoryRepository extends JpaRepository<StreamerHistory, Long> {

    /**
     * [메서드 역할]: 특정 스트리머의 ID를 기준으로 모든 기록을 시간 순(Ascending, 오름차순)으로 가져옵니다.
     * JPA 규칙: findBy + 필드명(StreamerId) + OrderBy + 정렬기준(RecordedAt) + 방식(Asc)
     */
    List<StreamerHistory> findByStreamerIdOrderByRecordedAtAsc(Long streamerId);
}