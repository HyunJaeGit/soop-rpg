package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.repository.WalletRepository;
import com.soop.soop_rpg.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final StockService stockService;
    private final WalletRepository walletRepository; // 추가

    @GetMapping("/")
    public String index(Model model) {
        // DB에서 실제 지갑 정보를 가져옵니다.
        Wallet wallet = walletRepository.findAll().get(0);

        model.addAttribute("streamers", stockService.getTop100Streamers());
        model.addAttribute("userRank", wallet.getUserRank()); // DB 데이터로 변경
        model.addAttribute("userGold", wallet.getBalance()); // DB 데이터로 변경

        return "index";
    }

    // [새로 추가] 매수 버튼을 눌렀을 때 실행될 로직
    @PostMapping("/buy")
    @ResponseBody
    public String buyStock(@RequestParam(name = "streamerId") Long streamerId) {
        System.out.println("🚀 매수 요청 확인: 스트리머 ID " + streamerId);
        return "success";
    }
}