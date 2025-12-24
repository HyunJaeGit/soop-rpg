package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.StreamerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * [클래스 역할]: 스트리머의 과거 기록(주가, 시청자 수)을 DB에 저장하고 꺼내오는 도구입니다.
 * JpaRepository를 상속받으면 count(), save(), findAll() 같은 메서드를 공짜로 쓸 수 있습니다.
 */
@Repository // 이 인터페이스가 DB와 소통하는 저장소임을 선언합니다.
public interface StreamerHistoryRepository extends JpaRepository<StreamerHistory, Long> {
    // count() 메서드는 JpaRepository가 기본적으로 제공하므로 따로 적지 않아도 됩니다.
}