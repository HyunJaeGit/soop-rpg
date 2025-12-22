package com.soop.soop_rpg.service;

import com.soop.soop_rpg.dto.StreamerStockDto;
import com.soop.soop_rpg.model.Portfolio;
import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.repository.PortfolioRepository;
import com.soop.soop_rpg.repository.StreamerRepository;
import com.soop.soop_rpg.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StreamerRepository streamerRepository;
    // 이 부분들이 필드로 선언되어야 위에서 @RequiredArgsConstructor가 자동으로 연결해줍니다.
    private final WalletRepository walletRepository; // 추가
    private final PortfolioRepository portfolioRepository; // 추가

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

}