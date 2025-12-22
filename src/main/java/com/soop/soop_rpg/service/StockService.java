package com.soop.soop_rpg.service;

import com.soop.soop_rpg.dto.StreamerStockDto;
import com.soop.soop_rpg.model.Portfolio;
import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.StreamerHistory;
import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.repository.HistoryRepository;
import com.soop.soop_rpg.repository.PortfolioRepository;
import com.soop.soop_rpg.repository.StreamerRepository;
import com.soop.soop_rpg.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StreamerRepository streamerRepository;
    // 이 부분들이 필드로 선언되어야 위에서 @RequiredArgsConstructor가 자동으로 연결해줍니다.
    private final WalletRepository walletRepository; // 추가
    private final PortfolioRepository portfolioRepository; // 추가
    private final HistoryRepository historyRepository;
    private final Random random = new Random();


    public List<StreamerStockDto> getTop100Streamers() {
        return streamerRepository.findAll().stream()
                .map(s -> new StreamerStockDto(
                        s.getId(), // [수정] ID 추가
                        s.getStreamerName(),
                        s.getCurrentViewers(),
                        (long) s.getCurrentPrice(),
                        s.getChangeRate(),
                        s.getRankName(),
                        "🌱"
                ))
                .toList();
    }

    @Transactional
    public boolean buyStock(Long streamerId) {
        // 1. 매수할 스트리머 정보 가져오기
        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));

        // 2. 내 지갑 정보 가져오기
        List<Wallet> wallets = walletRepository.findAll();
        if (wallets.isEmpty()) return false;

        Wallet wallet = wallets.get(0);

        // 3. 잔액 확인
        int price = streamer.getCurrentPrice();
        if (wallet.getBalance() < price) {
            return false;
        }

        // 4. 돈 깎기
        wallet.setBalance(wallet.getBalance() - price);
        // save를 굳이 안해도 @Transactional 덕분에 데이터가 변경되면 자동 저장(Dirty Checking)되지만 명시적으로 적어둡니다.
        walletRepository.save(wallet);

        // 5. 내 포트폴리오에 추가
        Portfolio portfolio = new Portfolio();
        portfolio.setStreamerName(streamer.getStreamerName());
        portfolio.setQuantity(1);
        portfolio.setAveragePrice((long) price);
        portfolioRepository.save(portfolio);

        return true;
    }


    // 지갑 정보 조회
    public Wallet getWallet() {
        List<Wallet> wallets = walletRepository.findAll();
        return wallets.isEmpty() ? null : wallets.get(0);
    }

    // 내 주식 목록 조회
    public List<Portfolio> getMyPortfolio() {
        return portfolioRepository.findAll();
    }

    @Transactional
    public void refreshStockPrices() {
        List<Streamer> streamers = streamerRepository.findAll();
        // 매번 전원을 다 바꾸면 느리니, 랜덤하게 10명 정도만 변동을 줍니다.
        Collections.shuffle(streamers);
        List<Streamer> targets = streamers.subList(0, 10);

        for (Streamer s : targets) {
            updateSingleStreamer(s);
        }
    }

    // 스트리머 한 명의 정보를 업데이트하고 기록하는 공통 메서드
    @Transactional
    public void updateSingleStreamer(Streamer s) {
        // 1. 시청자 수 랜덤 변동 (-15% ~ +15%)
        int change = (int) (s.getCurrentViewers() * (random.nextDouble() * 0.3 - 0.15));
        int newViewers = Math.max(0, s.getCurrentViewers() + change);
        s.setCurrentViewers(newViewers);

        // 2. 가격 계산 (시청자 1명당 10G + 기본가 100G)
        int newPrice = 100 + (newViewers / 10);
        s.setCurrentPrice(newPrice);

        // 3. 히스토리 기록
        StreamerHistory history = new StreamerHistory();
        history.setStreamerId(s.getId());
        history.setStreamerName(s.getStreamerName());
        history.setViewers(newViewers);
        history.setPrice(newPrice);
        history.setRecordedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    // 특정 스트리머 상세 정보 조회
    public Streamer getStreamerById(Long id) {
        return streamerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));
    }

    // 특정 스트리머의 최근 가격 히스토리 조회 (그래프용)
    public List<StreamerHistory> getStockHistory(Long streamerId) {
        // 최신순으로 가져와서 그래프에 뿌리기 위해 리스트를 뒤집거나 정렬해서 보낼 수 있습니다.
        return historyRepository.findTop20ByStreamerIdOrderByRecordedAtDesc(streamerId);
    }

    // 다량 매수 기능
    @Transactional
    public boolean buyStock(Long streamerId, int quantity) {
        if (quantity <= 0) return false;

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));

        Wallet wallet = getWallet();
        if (wallet == null) return false;

        long totalCost = (long) streamer.getCurrentPrice() * quantity;

        // 잔액 확인
        if (wallet.getBalance() < totalCost) {
            return false;
        }

        // 돈 차감
        wallet.setBalance(wallet.getBalance() - (int)totalCost);
        walletRepository.save(wallet);

        // 포트폴리오 업데이트 (이미 있으면 수량 추가, 없으면 신규 생성)
        Portfolio portfolio = portfolioRepository.findAll().stream()
                .filter(p -> p.getStreamerName().equals(streamer.getStreamerName()))
                .findFirst()
                .orElse(new Portfolio());

        if (portfolio.getId() == null) { // 신규 구매
            portfolio.setStreamerName(streamer.getStreamerName());
            portfolio.setQuantity(quantity);
            portfolio.setAveragePrice((long) streamer.getCurrentPrice());
        } else { // 추가 매수 (평단가 계산: (기존총액 + 신규총액) / 전체수량)
            long currentTotal = portfolio.getAveragePrice() * portfolio.getQuantity();
            int newQuantity = portfolio.getQuantity() + quantity;
            portfolio.setAveragePrice((currentTotal + totalCost) / newQuantity);
            portfolio.setQuantity(newQuantity);
        }

        portfolioRepository.save(portfolio);
        return true;
    }

    // 매도 기능
    @Transactional
    public boolean sellStock(Long streamerId, int quantity) {
        if (quantity <= 0) return false;

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));

        // 내 포트폴리오에서 해당 스트리머 찾기
        Portfolio portfolio = portfolioRepository.findAll().stream()
                .filter(p -> p.getStreamerName().equals(streamer.getStreamerName()))
                .findFirst()
                .orElse(null);

        // 보유 주식이 없거나 팔려는 수량이 더 많으면 실패
        if (portfolio == null || portfolio.getQuantity() < quantity) {
            return false;
        }

        Wallet wallet = getWallet();
        long totalGain = (long) streamer.getCurrentPrice() * quantity;

        // 1. 수량 차감 (다 팔면 삭제, 남으면 업데이트)
        if (portfolio.getQuantity() == quantity) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            portfolioRepository.save(portfolio);
        }

        // 2. 지갑에 돈 추가
        wallet.setBalance(wallet.getBalance() + (int)totalGain);
        walletRepository.save(wallet);

        return true;
    }

}