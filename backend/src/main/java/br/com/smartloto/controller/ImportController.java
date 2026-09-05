package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.ImportResponse;
import br.com.smartloto.service.CaixaImportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
@PreAuthorize("hasRole('ADMIN')")
public class ImportController {
    private final CaixaImportService service;

    public ImportController(CaixaImportService service){
        this.service=service;
    }

    @PostMapping("/{type}/sync")
    public ImportResponse sync(@PathVariable LotteryType type){
        return service.syncNow(type);
    }

    @PostMapping("/{type}/missing")
    public ImportResponse importMissing(@PathVariable LotteryType type){
        return service.importMissing(type);
    }

    @PostMapping("/{type}")
    public ImportResponse importDraws(
            @PathVariable LotteryType type,
            @RequestParam(defaultValue="200") int quantity
    ){
        return service.importRecent(type,Math.min(Math.max(quantity,1),1000));
    }
}
