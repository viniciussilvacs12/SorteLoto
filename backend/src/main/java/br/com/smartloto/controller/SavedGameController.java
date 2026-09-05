package br.com.smartloto.controller;

import br.com.smartloto.dto.*;
import br.com.smartloto.service.SavedGameService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-games")
public class SavedGameController {
    private final SavedGameService service;
    public SavedGameController(SavedGameService service){this.service=service;}

    @PostMapping
    public SavedGameResponse save(Authentication auth,@RequestBody SaveGameRequest request){
        return service.save(auth.getName(),request);
    }

    @GetMapping
    public List<SavedGameResponse> list(Authentication auth){
        return service.list(auth.getName());
    }

    @GetMapping("/ranking")
    public List<RankedGameResponse> ranking(Authentication auth){
        return service.ranking(auth.getName());
    }


    @DeleteMapping("/{id}")
    public void delete(Authentication auth,@PathVariable Long id){
        service.delete(auth.getName(),id);
    }

    @PatchMapping("/{id}/favorite")
    public SavedGameResponse favorite(Authentication auth,@PathVariable Long id,@RequestParam boolean value){
        return service.favorite(auth.getName(),id,value);
    }

    @GetMapping("/{id}/check")
    public CheckGameResponse check(Authentication auth,@PathVariable Long id){
        return service.check(auth.getName(),id);
    }
}
