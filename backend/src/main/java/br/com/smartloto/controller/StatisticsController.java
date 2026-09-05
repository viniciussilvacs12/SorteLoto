package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.StatsResponse;
import br.com.smartloto.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {
    private final StatisticsService service;
    public StatisticsController(StatisticsService service){this.service=service;}

    @GetMapping("/{type}")
    public StatsResponse stats(@PathVariable LotteryType type){ return service.stats(type); }
}
