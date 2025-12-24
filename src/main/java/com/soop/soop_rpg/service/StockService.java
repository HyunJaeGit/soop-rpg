package com.soop.soop_rpg.service;

import com.soop.soop_rpg.model.*;
import com.soop.soop_rpg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StreamerRepository streamerRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;

    // 메인 화면용: 시청자 많은 순으로 10명 조회
    public List<Streamer> getTop10Streamers() {
        return streamerRepository.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getCurrentViewers(), s1.getCurrentViewers()))
                .limit(10)
                .toList();
    }

    // 매수 로직 (수량 입력 방식)
    @Transactional
    public boolean buyStock(Long streamerId, int quantity) {
        Streamer streamer = streamerRepository.findById(streamerId).orElseThrow();
        Wallet wallet = walletRepository.findById(1L).orElseThrow();
        long totalCost = (long) streamer.getCurrentPrice() * quantity;

        if (wallet.getBalance() < totalCost) return false;

        wallet.setBalance(wallet.getBalance() - totalCost);

        Portfolio portfolio = portfolioRepository.findByStreamerName(streamer.getStreamerName())
                .orElse(new Portfolio(streamer.getStreamerName(), 0, 0L));

        // 평단가 계산
        long currentTotal = portfolio.getAveragePrice() * portfolio.getQuantity();
        int newQuantity = portfolio.getQuantity() + quantity;
        portfolio.setAveragePrice((currentTotal + totalCost) / newQuantity);
        portfolio.setQuantity(newQuantity);

        portfolioRepository.save(portfolio);
        return true;
    }

    // 매도 로직
    @Transactional
    public boolean sellStock(Long streamerId, int quantity) {
        Streamer streamer = streamerRepository.findById(streamerId).orElseThrow();
        Portfolio portfolio = portfolioRepository.findByStreamerName(streamer.getStreamerName()).orElse(null);

        if (portfolio == null || portfolio.getQuantity() < quantity) return false;

        Wallet wallet = walletRepository.findById(1L).orElseThrow();
        wallet.setBalance(wallet.getBalance() + (long) streamer.getCurrentPrice() * quantity);

        if (portfolio.getQuantity() == quantity) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            portfolioRepository.save(portfolio);
        }
        return true;
    }
}