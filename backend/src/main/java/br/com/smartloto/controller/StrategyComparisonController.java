package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.StrategyComparisonResponse;
import br.com.smartloto.service.StrategyComparisonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/strategy-comparison")
public class StrategyComparisonController {

    private final StrategyComparisonService service;

    public StrategyComparisonController(StrategyComparisonService service) {
        this.service = service;
    }

    @GetMapping("/{type}")
    public StrategyComparisonResponse compare(
            @PathVariable LotteryType type,
            @RequestParam(defaultValue = "30") int tests,
            @RequestParam(defaultValue = "200") int candidates
    ) {
        return service.compare(type, tests, candidates);
    }
}
