package br.com.smartloto.controller;

import br.com.smartloto.dto.ProfileResponse;
import br.com.smartloto.repository.AppUserRepository;
import br.com.smartloto.repository.SavedGameRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final AppUserRepository users;
    private final SavedGameRepository games;

    public ProfileController(AppUserRepository users,SavedGameRepository games){
        this.users=users;
        this.games=games;
    }

    @GetMapping("/me")
    public ProfileResponse me(Authentication auth){
        var user=users.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        return new ProfileResponse(
                user.getId(),user.getName(),user.getEmail(),user.getRole().name(),
                games.countByUserEmail(user.getEmail()),
                games.countByUserEmailAndFavoriteTrue(user.getEmail())
        );
    }
}
