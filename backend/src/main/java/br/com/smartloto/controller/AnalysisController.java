package br.com.smartloto.controller;

import br.com.smartloto.dto.AnalysisRequest;
import br.com.smartloto.dto.AnalysisResponse;
import br.com.smartloto.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public AnalysisResponse analyze(@Valid @RequestBody AnalysisRequest request) {
        return analysisService.analyze(request);
    }
}
