package com.soop.soop_rpg.config;

import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) {
        // 지갑이 없을 때만 초기 자금 생성
        if (walletRepository.count() == 0) {
            walletRepository.save(new Wallet(1000000L, "새싹 투자자"));
            System.out.println("💰 초기 자본금 1,000,000G 지급 완료!");
        }
        // 스트리머 생성 로직은 삭제했습니다. ApiService가 담당합니다.
    }
}