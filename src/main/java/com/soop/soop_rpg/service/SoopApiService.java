package com.soop.soop_rpg.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoopApiService {

    // SOOP API 주소 (예시)
    private final String SOOP_API_URL = "https://api.soop.com/live/list";

    public void updateStreamerData() {
        // 1. 외부 API 호출
        // 2. 응답 데이터(JSON) 분석
        // 3. 기존 DB의 Streamer 정보 업데이트 (시청자 수, 가격 등)
        // 4. StreamerHistory에 현재 상태 기록
    }
}