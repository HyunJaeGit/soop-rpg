package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.repository.StreamerHistoryRepository;
import com.soop.soop_rpg.service.SoopApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [클래스 역할]: 2단계 개발 중에 로직이 정상적으로 작동하는지 확인하기 위한 테스트 전용 컨트롤러입니다.
 * 뷰(HTML)를 거치지 않고 웹 브라우저에 텍스트를 직접 출력하여 데이터의 상태를 보여줍니다.
 */
@RestController // JSON이나 문자열 데이터를 브라우저에 직접 응답하는 컨트롤러임을 선언합니다.
@RequestMapping("/test") // 이 클래스 내의 모든 주소는 앞에 "/test"가 붙습니다. (예: /test/update)
@RequiredArgsConstructor // final이 붙은 서비스와 리포지토리를 스프링이 자동으로 연결(주입)해줍니다.
public class TestController {

    private final SoopApiService soopApiService; // 실시간 데이터 수집 서비스
    private final StreamerHistoryRepository streamerHistoryRepository; // 시계열 기록 저장소

    /**
     * [테스트 1]: 단순히 SOOP API를 호출해서 스트리머 정보를 최신화합니다.
     * 접속 주소: http://localhost:8080/test/update
     */
    @GetMapping("/update")
    public String testUpdate() {
        try {
            soopApiService.updateMarketWithRealData();
            return "✅ [성공] SOOP 실시간 데이터가 스트리머 목록에 반영되었습니다.";
        } catch (Exception e) {
            return "❌ [실패] 데이터 업데이트 중 에러 발생: " + e.getMessage();
        }
    }

    /**
     * [테스트 2]: 스케줄러가 할 일을 수동으로 실행해보고, 히스토리(그래프용 데이터)가 잘 쌓이는지 확인합니다.
     * 접속 주소: http://localhost:8080/test/scheduler-check
     */
    @GetMapping("/scheduler-check")
    public String checkSchedulerLogic() {
        try {
            // 1. 실시간 데이터 수집 실행
            soopApiService.updateMarketWithRealData();

            // 2. 현재 DB에 저장된 전체 히스토리 데이터 개수를 가져옵니다.
            long historyCount = streamerHistoryRepository.count();

            // 3. 결과를 브라우저에 출력합니다.
            return "<h2>📊 스케줄러 로직 테스트 결과</h2>" +
                    "<p>✅ 현재가 업데이트 및 히스토리 기록 완료!</p>" +
                    "<p>📈 현재 DB에 쌓인 전체 히스토리 개수: <b>" + historyCount + "개</b></p>" +
                    "<p>💡 새로고침을 할 때마다 스트리머 수(약 100개)만큼 숫자가 늘어난다면 정상입니다.</p>";
        } catch (Exception e) {
            return "[실패] 스케줄러 로직 실행 중 에러 발생: " + e.getMessage();
        }
    }
}