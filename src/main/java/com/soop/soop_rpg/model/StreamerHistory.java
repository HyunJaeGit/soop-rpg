package com.soop.soop_rpg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class StreamerHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long streamerId;     // 어느 스트리머의 기록인가? (ID로 연결)
    private String streamerName; // 이름도 같이 저장하면 조회할 때 편합니다.
    private int viewers;         // 당시 시청자 수
    private int price;           // 당시 주가
    private LocalDateTime recordedAt; // 기록된 시간
}