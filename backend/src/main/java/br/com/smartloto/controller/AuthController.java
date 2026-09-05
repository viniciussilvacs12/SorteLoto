package br.com.smartloto.controller;

import br.com.smartloto.dto.*;
import br.com.smartloto.service.AuthService;
import br.com.smartloto.service.GoogleAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    public AuthController(AuthService authService, GoogleAuthService googleAuthService){
        this.authService=authService;
        this.googleAuthService=googleAuthService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/google/config")
    public GoogleConfigResponse googleConfig(){
        return new GoogleConfigResponse(
                googleAuthService.enabled(),
                googleAuthService.clientId()
        );
    }

    @PostMapping("/google")
    public AuthResponse google(@RequestBody GoogleCredentialRequest request){
        return googleAuthService.login(request.credential());
    }
}
