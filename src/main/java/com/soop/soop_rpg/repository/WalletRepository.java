package com.soop.soop_rpg.repository;

import com.soop.soop_rpg.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}