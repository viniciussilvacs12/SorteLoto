package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.SyncStatusResponse;
import br.com.smartloto.service.CaixaImportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync-status")
public class SyncStatusController {
    private final CaixaImportService service;

    public SyncStatusController(CaixaImportService service){
        this.service=service;
    }

    @GetMapping("/{type}")
    public SyncStatusResponse status(@PathVariable LotteryType type){
        return service.status(type);
    }
}
