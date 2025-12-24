package com.soop.soop_rpg.service;

import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [클래스 역할]: 사용자의 지갑 잔액을 조회하고, 돈을 쓰거나 버는 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * [메서드 역할]: 내 지갑 정보를 가져옵니다. 없으면 기본 지갑을 생성합니다.
     */
    @Transactional
    public Wallet getMyWallet() {
        // ID 1번 지갑을 찾고, 없으면 새로 만들어서 저장함 (초기 자금 100만G)
        return walletRepository.findById(1L).orElseGet(() -> {
            Wallet newWallet = new Wallet();
            newWallet.setBalance(1000000L); // 초기 자금
            newWallet.setUserRank("건빵");
            return walletRepository.save(newWallet);
        });
    }

    /**
     * [메서드 역할]: 주식을 살 때 지갑에서 돈을 차감합니다.
     */
    @Transactional
    public void decreaseBalance(long amount) {
        Wallet wallet = getMyWallet();
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("잔액이 부족합니다!");
        }
        wallet.setBalance(wallet.getBalance() - amount);
    }
}