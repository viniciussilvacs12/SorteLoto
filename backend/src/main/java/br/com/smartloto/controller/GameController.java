package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.GameResponse;
import br.com.smartloto.service.LotteryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final LotteryService lotteryService;

    public GameController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping("/mega-sena")
    public GameResponse megaSena() {
        return lotteryService.generate(LotteryType.MEGA_SENA);
    }

    @GetMapping("/lotofacil")
    public GameResponse lotofacil() {
        return lotteryService.generate(LotteryType.LOTOFACIL);
    }

    @GetMapping("/history")
    public List<GameResponse> history() {
        return lotteryService.history();
    }
}
