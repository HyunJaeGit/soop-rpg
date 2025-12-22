package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.model.Portfolio;
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
        // 1. 지갑 정보 가져오기 (Service 이용)
        Wallet wallet = stockService.getWallet();

        if (wallet == null) {
            model.addAttribute("userRank", "데이터 로딩 중...");
            model.addAttribute("userGold", 0);
        } else {
            model.addAttribute("userRank", wallet.getUserRank());
            model.addAttribute("userGold", wallet.getBalance());
        }

        // 2. 스트리머 목록 가져오기
        model.addAttribute("streamers", stockService.getTop100Streamers());

        // 3. 내 포트폴리오(산 주식) 목록 가져오기
        model.addAttribute("myStocks", stockService.getMyPortfolio());

        return "index";
    }

    // 매수 기능
    @PostMapping("/buy")
    @ResponseBody
    public String buyStock(@RequestParam("streamerId") Long streamerId) {
        boolean success = stockService.buyStock(streamerId);
        return success ? "success" : "fail";
    }


}