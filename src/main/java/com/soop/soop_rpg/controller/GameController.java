package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final StockService stockService;

    @GetMapping("/")
    public String index(Model model) {
        // 상위 30인 데이터를 모델에 담아 화면(Thymeleaf)으로 전송
        model.addAttribute("streamers", stockService.getTop100Streamers());
        model.addAttribute("userRank", "새싹 투자자");
        model.addAttribute("userGold", 1000000); // 초기 자본 100만 골드

        return "index"; // src/main/resources/templates/index.html 을 찾아감
    }
}