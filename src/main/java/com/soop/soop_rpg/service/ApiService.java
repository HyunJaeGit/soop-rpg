package com.soop.soop_rpg.service;

import com.soop.soop_rpg.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * [클래스 역할]: 상위 10명의 스트리머 정보를 크롤링하여 DB를 업데이트합니다.
 */
@Service
@RequiredArgsConstructor
public class ApiService {

    private final StreamerRepository streamerRepository;

    /**
     * [메서드 역할]: SOOP 라이브 페이지에서 상위 10명 데이터를 가져와 업데이트합니다.
     */
    @Transactional
    public void syncTopStreamerData() {
        String url = "https://www.sooplive.co.kr/?type=all";

        try {
            // 브라우저인 척 접속하여 차단을 방지합니다.
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get();

            // 방송 리스트 요소를 찾습니다.
            Elements streamerElements = doc.select("li.broad_item");
            int count = 0;

            for (Element el : streamerElements) {
                if (count >= 10) break; // [규칙]: 상위 10명만 처리합니다.

                String nickname = el.select(".nick").text();
                String viewersStr = el.select(".v_cnt").text();
                int viewers = parseViewers(viewersStr);

                // DB 업데이트 로직 호출
                updateOrSkip(nickname, viewers);
                count++;
            }
        } catch (IOException e) {
            System.err.println("데이터 동기화 에러: " + e.getMessage());
        }
    }

    /**
     * [메서드 역할]: DB에 스트리머가 있으면 시청자 수와 주가를 업데이트합니다.
     */
    private void updateOrSkip(String nickname, int viewers) {
        streamerRepository.findByStreamerName(nickname).ifPresent(streamer -> {
            streamer.setCurrentViewers(viewers);

            // 시청자 1명당 10G로 계산하는 단순 공식 (추후 수정 가능)
            int calculatedPrice = 1000 + (viewers / 10);
            streamer.setCurrentPrice(calculatedPrice);

            System.out.println("[API 동기화] " + nickname + " 업데이트 완료");
        });
    }

    private int parseViewers(String text) {
        if (text == null || text.isEmpty()) return 0;
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
}