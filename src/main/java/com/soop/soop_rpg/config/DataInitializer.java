package com.soop.soop_rpg.config;

import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.Wallet;
import com.soop.soop_rpg.repository.StreamerRepository;
import com.soop.soop_rpg.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StreamerRepository streamerRepository;
    private final WalletRepository walletRepository; // [추가] 필드 선언 (final 필수)

    @Override
    public void run(String... args) {
        // DB에 데이터가 없을 때만 100명을 생성합니다.
        if (streamerRepository.count() == 0) {
            Random random = new Random();
            for (int i = 1; i <= 100; i++) {
                int viewers = 500 + random.nextInt(99500);
                int price = viewers / 10;

                String rank = "잡주";
                if (i <= 10) rank = "대장주";
                else if (i <= 30) rank = "우량주";

                streamerRepository.save(new Streamer(
                        "스트리머 " + i,
                        rank,
                        price,
                        0.0, // 처음엔 변동률 0
                        viewers
                ));
            }
            System.out.println("✅ 100명의 스트리머 데이터가 DB에 저장되었습니다.");
        }

        if (walletRepository.count() == 0) {
            walletRepository.save(new Wallet(1000000L, "새싹 투자자"));
            System.out.println("✅ 초기 자본금 1,000,000G가 지급되었습니다.");
        }
    }
}