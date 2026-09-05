package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.AnalysisRequest;
import br.com.smartloto.dto.SmartAnalysisResponse;
import br.com.smartloto.service.SmartAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/smart-analysis")
public class SmartAnalysisController {

    private final SmartAnalysisService service;

    public SmartAnalysisController(SmartAnalysisService service) {
        this.service = service;
    }

    @PostMapping
    public SmartAnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
        return service.analyze(request.lotteryType(), request.numbers());
    }
}
