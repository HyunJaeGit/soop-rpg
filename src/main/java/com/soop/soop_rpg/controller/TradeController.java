package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * [클래스 역할]: 사용자의 주식 거래(매수/매도) 요청을 처리하는 컨트롤러입니다.
 * 화면 이동 없이 데이터만 주고받기 위해 @RestController를 사용합니다.
 */
@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final StockService stockService;

    /**
     * [메서드 역할]: 주식을 구매하거나 판매합니다.
     * @param request: 클라이언트에서 보낸 데이터 (type: "buy" 또는 "sell", id: 스트리머ID, quantity: 수량)
     */
    @PostMapping
    public ResponseEntity<?> handleTrade(@RequestBody Map<String, Object> request) {
        try {
            String type = (String) request.get("type");
            Long streamerId = Long.valueOf(request.get("id").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());

            boolean success = false;

            if ("buy".equals(type)) {
                // 수량 입력을 지원하는 매수 로직 호출
                success = stockService.buyStock(streamerId, quantity);
            } else if ("sell".equals(type)) {
                // 수량 입력을 지원하는 매도 로직 호출
                success = stockService.sellStock(streamerId, quantity);
            }

            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "거래가 성공적으로 완료되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "잔액이 부족하거나 보유 수량이 모자랍니다."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "에러 발생: " + e.getMessage()));
        }
    }
}