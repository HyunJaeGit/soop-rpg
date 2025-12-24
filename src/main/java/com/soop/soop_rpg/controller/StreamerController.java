package com.soop.soop_rpg.controller;

import com.soop.soop_rpg.model.Streamer;
import com.soop.soop_rpg.model.StreamerHistory;
import com.soop.soop_rpg.repository.StreamerHistoryRepository;
import com.soop.soop_rpg.repository.StreamerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

/**
 * [클래스 역할]: 스트리머 상세 정보를 화면에 보여주는 컨트롤러입니다.
 */
@Controller
@RequiredArgsConstructor
public class StreamerController {

    private final StreamerRepository streamerRepository;
    private final StreamerHistoryRepository historyRepository;

    /**
     * [메서드 역할]: /streamer/1 같은 주소로 들어오면 해당 스트리머 정보를 보여줍니다.
     * @PathVariable Long id: 주소창에 있는 숫자(id)를 변수로 가져옵니다.
     */
    @GetMapping("/streamer/{id}")
    public String detail(@PathVariable("id") Long id, Model model) { // ("id")를 명시해주면 더 확실합니다.

        Streamer streamer = streamerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스트리머입니다. ID: " + id));

        List<StreamerHistory> history = historyRepository.findByStreamerIdOrderByRecordedAtAsc(id);

        model.addAttribute("streamer", streamer);
        model.addAttribute("history", history);

        return "detail";
    }
}