package br.com.smartloto.service;

import br.com.smartloto.domain.AppUser;
import br.com.smartloto.domain.UserRole;
import br.com.smartloto.dto.*;
import br.com.smartloto.repository.AppUserRepository;
import br.com.smartloto.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }

    public AuthResponse register(RegisterRequest request){
        if(request.email() == null || request.password() == null || request.password().length() < 8)
            throw new IllegalArgumentException("Informe e-mail e uma senha com pelo menos 8 caracteres.");

        String email=request.email().trim().toLowerCase();

        if(repository.existsByEmail(email))
            throw new IllegalArgumentException("E-mail já cadastrado.");

        AppUser user=repository.save(new AppUser(
                request.name(),
                email,
                passwordEncoder.encode(request.password()),
                UserRole.USER
        ));
        return response(user);
    }

    public AuthResponse login(LoginRequest request){
        String email=request.email().trim().toLowerCase();
        AppUser user=repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if(!passwordEncoder.matches(request.password(),user.getPasswordHash()))
            throw new IllegalArgumentException("Credenciais inválidas.");

        return response(user);
    }

    private AuthResponse response(AppUser user){
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                jwtService.generate(user)
        );
    }
}
