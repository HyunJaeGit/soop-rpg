package com.soop.soop_rpg.service;

import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [클래스 역할]: SOOP의 실시간 방송 목록 API(XML)를 호출하여 진짜 상위 10명 데이터를 가져옵니다.
 */
@Service
@RequiredArgsConstructor
public class ApiService {

    private final StreamerRepository streamerRepository;

    @Transactional
    public void syncTopStreamerData() {
        // [수정]: 화면 크롤링 대신, 데이터를 직접 주는 XML 주소를 사용합니다.
        String url = "https://live.sooplive.co.kr/afreeca/broad_list_xml.php?selectType=all&orderType=view_cnt&pageNo=1";

        try {
            // XML 데이터를 가져옵니다.
            Document doc = Jsoup.connect(url)
                    .timeout(5000)
                    .get();

            // XML 태그 중 <broad> 태그들을 찾습니다.
            Elements broads = doc.select("broad");
            int count = 0;

            if (broads.isEmpty()) {
                System.out.println("⚠️ 가져온 방송 데이터가 없습니다. URL을 확인하세요.");
                return;
            }

            for (Element broad : broads) {
                if (count >= 10) break; // 상위 10명 제한

                // XML 내의 태그명에 맞춰 데이터를 추출합니다.
                String nickname = broad.select("user_nick").text();   // 스트리머 닉네임
                String viewersStr = broad.select("total_view_cnt").text(); // 시청자 수
                int viewers = Integer.parseInt(viewersStr.isEmpty() ? "0" : viewersStr);

                if (!nickname.isEmpty()) {
                    updateOrSave(nickname, viewers);
                    count++;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 데이터 동기화 중 에러 발생: " + e.getMessage());
        }
    }

    /**
     * [메서드 역할]: 닉네임을 확인하여 DB에 없으면 신규 생성, 있으면 업데이트합니다.
     */
    private void updateOrSave(String nickname, int viewers) {
        Streamer streamer = streamerRepository.findByStreamerName(nickname)
                .orElseGet(() -> {
                    Streamer newS = new Streamer();
                    newS.setStreamerName(nickname);
                    newS.setRankName("대장주"); // 기본 등급
                    return newS;
                });

        streamer.setCurrentViewers(viewers);
        // 주가 산정 로직: 기본 1000G + 시청자 10명당 1G
        streamer.setCurrentPrice(1000 + (viewers / 10));

        streamerRepository.save(streamer);
        System.out.println("✅ [진짜 데이터 반영] " + nickname + " (" + viewers + "명)");
    }
}