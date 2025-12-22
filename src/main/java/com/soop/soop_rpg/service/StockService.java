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

/**
 * 주식 게임의 핵심 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final StreamerRepository streamerRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final HistoryRepository historyRepository;
    private final Random random = new Random();

    /**
     * 메인 페이지용 상위 10명 스트리머 목록 조회
     * 시청자 수 기준 내림차순 정렬 후 10명만 추출
     */
    public List<StreamerStockDto> getTopStreamers() {
        return streamerRepository.findAll().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getCurrentViewers(), s1.getCurrentViewers()))
                .limit(10)
                .map(s -> new StreamerStockDto(
                        s.getId(),
                        s.getStreamerName(),
                        s.getCurrentViewers(),
                        (long) s.getCurrentPrice(),
                        s.getChangeRate(),
                        s.getRankName(),
                        "🌱"
                ))
                .toList();
    }

    /**
     * 사용자 지갑 정보 조회 (첫 번째 지갑 정보를 가져옴)
     */
    public Wallet getWallet() {
        List<Wallet> wallets = walletRepository.findAll();
        // Java 21: get(0) 대신 getFirst() 사용
        return wallets.isEmpty() ? null : wallets.getFirst();
    }

    /**
     * 실시간 주가 갱신 로직
     * 랜덤하게 10명을 뽑아 시청자 수와 가격을 변동시킴
     */
    @Transactional
    public void refreshStockPrices() {
        List<Streamer> allStreamers = streamerRepository.findAll();

        // 데이터가 없을 경우 IndexOutOfBoundsException 방지를 위한 방어 코드
        if (allStreamers.isEmpty()) {
            return;
        }

        // 리스트를 무작위로 섞음
        Collections.shuffle(allStreamers);

        // 전체 크기와 10 중 작은 값을 선택 (데이터가 10개 미만일 때 에러 방지)
        int limit = Math.min(allStreamers.size(), 10);
        List<Streamer> targets = allStreamers.subList(0, limit);

        for (Streamer s : targets) {
            updateSingleStreamer(s);
        }
    }

    /**
     * 개별 스트리머의 시청자 수 및 가격 업데이트와 히스토리 저장
     */
    @Transactional
    public void updateSingleStreamer(Streamer s) {
        // 1. 시청자 수 변동: 기존 시청자의 -15% ~ +15% 사이 랜덤
        int change = (int) (s.getCurrentViewers() * (random.nextDouble() * 0.3 - 0.15));
        int newViewers = Math.max(0, s.getCurrentViewers() + change);
        s.setCurrentViewers(newViewers);

        // 2. 가격 계산 로직: 시청자 10명당 1G + 기본가 100G
        int newPrice = 100 + (newViewers / 10);
        s.setCurrentPrice(newPrice);

        // 3. 차트용 히스토리 기록 생성 및 저장
        StreamerHistory history = new StreamerHistory();
        history.setStreamerId(s.getId());
        history.setStreamerName(s.getStreamerName());
        history.setViewers(newViewers);
        history.setPrice(newPrice);
        history.setRecordedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    /**
     * 주식 1주 즉시 매수 기능
     */
    @Transactional
    public boolean buyStock(Long streamerId) {
        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));

        Wallet wallet = getWallet();
        if (wallet == null) return false;

        int price = streamer.getCurrentPrice();
        if (wallet.getBalance() < price) {
            return false; // 잔액 부족
        }

        // 잔액 차감
        wallet.setBalance(wallet.getBalance() - price);
        walletRepository.save(wallet);

        // 포트폴리오(내 주식함)에 저장
        Portfolio portfolio = new Portfolio();
        portfolio.setStreamerName(streamer.getStreamerName());
        portfolio.setQuantity(1);
        portfolio.setAveragePrice((long) price);
        portfolioRepository.save(portfolio);

        return true;
    }

    /**
     * 내 보유 주식(포트폴리오) 전체 목록 조회
     */
    public List<Portfolio> getMyPortfolio() {
        return portfolioRepository.findAll();
    }

    /**
     * 상세 페이지용 스트리머 정보 조회
     */
    public Streamer getStreamerById(Long id) {
        return streamerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));
    }

    /**
     * 특정 스트리머의 최근 가격 기록 20개 조회 (그래프 데이터용)
     */
    public List<StreamerHistory> getStockHistory(Long streamerId) {
        return historyRepository.findTop20ByStreamerIdOrderByRecordedAtDesc(streamerId);
    }

    /**
     * 다량 매수 기능 (수량 입력 매수)
     * 평단가(Average Price) 계산 로직 포함
     */
    @Transactional
    public boolean buyStock(Long streamerId, int quantity) {
        if (quantity <= 0) return false;

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));

        Wallet wallet = getWallet();
        if (wallet == null) return false;

        long totalCost = (long) streamer.getCurrentPrice() * quantity;

        if (wallet.getBalance() < totalCost) {
            return false; // 잔액 부족
        }

        // 자산 차감
        wallet.setBalance(wallet.getBalance() - totalCost);
        walletRepository.save(wallet);

        // 기존 보유 주식 확인
        Portfolio portfolio = portfolioRepository.findAll().stream()
                .filter(p -> p.getStreamerName().equals(streamer.getStreamerName()))
                .findFirst()
                .orElse(new Portfolio());

        if (portfolio.getId() == null) { // 신규 구매
            portfolio.setStreamerName(streamer.getStreamerName());
            portfolio.setQuantity(quantity);
            portfolio.setAveragePrice((long) streamer.getCurrentPrice());
        } else { // 추가 매수 (평단가 재계산)
            long currentTotal = portfolio.getAveragePrice() * portfolio.getQuantity();
            int newQuantity = portfolio.getQuantity() + quantity;
            portfolio.setAveragePrice((currentTotal + totalCost) / newQuantity);
            portfolio.setQuantity(newQuantity);
        }

        portfolioRepository.save(portfolio);
        return true;
    }

    /**
     * 주식 매도 기능
     * 현재가 기준으로 현금화 및 포트폴리오 업데이트
     */
    @Transactional
    public boolean sellStock(Long streamerId, int quantity) {
        if (quantity <= 0) return false;

        Streamer streamer = streamerRepository.findById(streamerId)
                .orElseThrow(() -> new RuntimeException("스트리머를 찾을 수 없습니다."));

        Portfolio portfolio = portfolioRepository.findAll().stream()
                .filter(p -> p.getStreamerName().equals(streamer.getStreamerName()))
                .findFirst()
                .orElse(null);

        // 보유 수량 확인
        if (portfolio == null || portfolio.getQuantity() < quantity) {
            return false;
        }

        Wallet wallet = getWallet();
        long totalGain = (long) streamer.getCurrentPrice() * quantity;

        // 전량 매도 시 삭제, 일부 매도 시 수량만 업데이트
        if (portfolio.getQuantity() == quantity) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(portfolio.getQuantity() - quantity);
            portfolioRepository.save(portfolio);
        }

        // 지갑 잔액 증가
        wallet.setBalance(wallet.getBalance() + totalGain);
        walletRepository.save(wallet);

        return true;
    }
}