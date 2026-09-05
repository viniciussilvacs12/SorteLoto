package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.BacktestResult;
import br.com.smartloto.service.BacktestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backtest")
public class BacktestController {

    private final BacktestService service;

    public BacktestController(BacktestService service) {
        this.service = service;
    }

    @GetMapping("/{type}")
    public BacktestResult run(
            @PathVariable LotteryType type,
            @RequestParam(defaultValue = "30") int tests,
            @RequestParam(defaultValue = "200") int candidates
    ) {
        return service.run(type, tests, candidates);
    }
}
