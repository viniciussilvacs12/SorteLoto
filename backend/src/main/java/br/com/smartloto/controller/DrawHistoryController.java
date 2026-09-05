package br.com.smartloto.controller;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.DrawPageResponse;
import br.com.smartloto.dto.DrawResponse;
import br.com.smartloto.service.DrawHistoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/draws")
public class DrawHistoryController {
    private final DrawHistoryService service;

    public DrawHistoryController(DrawHistoryService service){
        this.service=service;
    }

    @GetMapping("/{type}")
    public DrawPageResponse list(
            @PathVariable LotteryType type,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size
    ){
        return service.list(type,page,size);
    }

    @GetMapping("/{type}/{contest}")
    public DrawResponse find(
            @PathVariable LotteryType type,
            @PathVariable int contest
    ){
        return service.find(type,contest);
    }
}
