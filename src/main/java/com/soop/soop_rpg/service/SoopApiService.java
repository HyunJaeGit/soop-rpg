package com.soop.soop_rpg.service;

import com.soop.soop_rpg.dto.SoopStreamerResponse;
import com.soop.soop_rpg.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * [클래스 역할]: 외부 API(SOOP)와 통신하여 데이터를 가져오고 가공하는 비즈니스 로직 담당 클래스입니다.
 */
@Service // 이 클래스가 "비즈니스 로직을 수행하는 서비스 객체"임을 스프링에게 알립니다.
@RequiredArgsConstructor // 클래스 안의 final이 붙은 변수(도구들)를 자동으로 연결(주입)해줍니다.
public class SoopApiService {

    private final RestTemplate restTemplate; // 아까 설정한 외부 통신 도구
    private final StreamerRepository streamerRepository; // 우리 DB에 접근하는 도구

    /**
     * [메서드 역할]: 실제 SOOP 데이터를 가져와서 우리 게임의 주가를 업데이트합니다.
     * @Transactional: 이 메서드 안의 작업(DB 수정 등)이 하나라도 실패하면 전부 취소(롤백)해서 안전하게 보호합니다.
     */
    @Transactional
    public void updateMarketWithRealData() {
        // 1. 헤더 설정: "우리는 공부 중인 프로젝트입니다"라고 정중하게 우리 정보를 담습니다.
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "SOOP-RPG-Learning-Project; contact: developer@example.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. SOOP 실시간 리스트 API 주소
        String url = "https://live.sooplive.co.kr/api/main_list.php";

        try {
            // 3. 실제로 인터넷을 통해 데이터를 요청합니다. (전화 걸기)
            ResponseEntity<SoopStreamerResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, SoopStreamerResponse.class);

            // 4. 응답 받은 데이터가 비어있지 않다면 하나씩 살펴봅니다.
            if (response.getBody() != null && response.getBody().broadList() != null) {
                response.getBody().broadList().forEach(info -> {

                    // 5. 우리 DB에서 이 스트리머가 있는지 이름으로 찾아봅니다.
                    streamerRepository.findByStreamerName(info.nickname()).ifPresent(s -> {
                        // 6. 찾았다면, 시청자 수를 실시간 정보로 업데이트합니다.
                        s.setCurrentViewers(info.viewCount());

                        // 7. [주가 산정 로직]: 시청자 수에 따라 가격을 결정합니다. (예: 기본 1000G + 시청자 10명당 1G)
                        int newPrice = 1000 + (info.viewCount() / 10);
                        s.setCurrentPrice(newPrice);
                    });
                });
                System.out.println(">>> [알림] 실시간 SOOP 데이터로 주식 시장이 갱신되었습니다!");
            }
        } catch (Exception e) {
            // 인터넷 연결 실패 등 에러가 발생했을 때 처리합니다.
            System.err.println(">>> [에러] 데이터 수집 중 문제가 발생했습니다: " + e.getMessage());
        }
    }
}