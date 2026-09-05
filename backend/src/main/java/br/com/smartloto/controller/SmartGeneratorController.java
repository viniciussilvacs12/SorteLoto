package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.SmartAnalysisResponse;
import br.com.smartloto.service.SmartGeneratorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smart-generator")
public class SmartGeneratorController {

    private final SmartGeneratorService service;

    public SmartGeneratorController(SmartGeneratorService service) {
        this.service = service;
    }

    @GetMapping("/{type}")
    public SmartAnalysisResponse generate(
            @PathVariable LotteryType type,
            @RequestParam(defaultValue = "500") int candidates
    ) {
        return service.generateBest(type, candidates);
    }
}
