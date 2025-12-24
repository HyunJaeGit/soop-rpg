package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.StreamerHistory;
import com.soop.soop_rpg.repository.PortfolioRepository;
import com.soop.soop_rpg.repository.StreamerHistoryRepository;
import com.soop.soop_rpg.repository.StreamerRepository;
import com.soop.soop_rpg.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * [클래스 역할]: 게임의 메인 화면과 상세 화면 연결을 담당하는 메인 컨트롤러입니다.
 * 중복되었던 MarketController와 StreamerController의 기능을 통합했습니다.
 */
@Controller
@RequiredArgsConstructor
public class GameController {

    private final StreamerRepository streamerRepository;
    private final StreamerHistoryRepository historyRepository;
    private final WalletService walletService;
    private final PortfolioRepository portfolioRepository;

    /**
     * [메서드]: 메인 페이지 (/)
     * 스트리머 목록, 내 지갑, 내 포트폴리오 정보를 한 번에 보여줍니다.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("streamers", streamerRepository.findAll());
        model.addAttribute("wallet", walletService.getMyWallet());
        model.addAttribute("portfolio", portfolioRepository.findAll());
        return "index";
    }

    /**
     * [메서드]: 상세 페이지 (/detail/{id})
     * 특정 스트리머의 현재 정보와 가격 히스토리(그래프용)를 보여줍니다.
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        // 1. 스트리머 기본 정보 조회
        Streamer streamer = streamerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스트리머입니다. ID: " + id));

        // 2. 그래프를 위한 가격 히스토리 조회
        List<StreamerHistory> history = historyRepository.findByStreamerIdOrderByRecordedAtAsc(id);

        model.addAttribute("streamer", streamer);
        model.addAttribute("history", history);
        model.addAttribute("wallet", walletService.getMyWallet());

        return "detail";
    }
}