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
     * [메서드 역할]: 현재 사용자의 지갑 정보를 가져옵니다.
     * (현재는 로그인이 없으므로 ID가 1번인 지갑을 기본으로 사용합니다.)
     */
    @Transactional(readOnly = true)
    public Wallet getMyWallet() {
        return walletRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("지갑을 찾을 수 없습니다."));
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