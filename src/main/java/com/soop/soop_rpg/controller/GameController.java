package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.StreamerHistory;
import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final StockService stockService;

    @GetMapping("/")
    public String index(Model model) {
        Wallet wallet = stockService.getWallet();
        // 지갑이 없으면 기본값을 가진 객체를 생성해서 넘김 (에러 방지)
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setBalance(0L);
            wallet.setUserRank("건빵");
        }
        model.addAttribute("wallet", wallet);
        model.addAttribute("streamers", stockService.getTopStreamers());
        model.addAttribute("portfolio", stockService.getMyPortfolio());

        return "index";
    }

    /**
     * 매수 기능 통합 (중복 제거됨)
     * - 메인 페이지에서 클릭 시: quantity가 없으므로 기본값 1주 매수
     * - 상세 페이지에서 클릭 시: 입력한 quantity만큼 매수
     */
    @PostMapping("/buy")
    @ResponseBody
    public String buyStock(@RequestParam("streamerId") Long streamerId,
                           @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        boolean success = stockService.buyStock(streamerId, quantity);
        return success ? "success" : "fail";
    }

    // 해당 스트리머 상세 조회 기능
    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        // 1. 해당 스트리머 정보 가져오기
        Streamer streamer = stockService.getStreamerById(id);

        // 2. 그래프를 그릴 히스토리 데이터 가져오기
        List<StreamerHistory> history = stockService.getStockHistory(id);

        model.addAttribute("streamer", streamer);
        model.addAttribute("history", history);

        return "detail";
    }

    // 매도 요청을 받는 주소
    @PostMapping("/sell")
    @ResponseBody
    public String sellStock(@RequestParam("streamerId") Long streamerId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {
        boolean success = stockService.sellStock(streamerId, quantity);
        return success ? "success" : "fail";
    }

}