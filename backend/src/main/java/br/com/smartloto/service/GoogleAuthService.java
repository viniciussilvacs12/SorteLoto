package br.com.smartloto.service;

import br.com.smartloto.domain.AppUser;
import br.com.smartloto.domain.UserRole;
import br.com.smartloto.dto.AuthResponse;
import br.com.smartloto.repository.AppUserRepository;
import br.com.smartloto.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class GoogleAuthService {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String clientId;

    public GoogleAuthService(
            AppUserRepository users,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${sorteloto.google.client-id:}") String clientId
    ){
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.clientId = clientId == null ? "" : clientId.trim();
    }

    public boolean enabled(){
        return !clientId.isBlank();
    }

    public String clientId(){
        return enabled() ? clientId : "";
    }

    public AuthResponse login(String credential){
        if(!enabled()){
            throw new IllegalStateException("Login com Google ainda não foi configurado.");
        }
        if(credential == null || credential.isBlank()){
            throw new IllegalArgumentException("Credencial Google ausente.");
        }

        try{
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(clientId)).build();

            GoogleIdToken token = verifier.verify(credential);
            if(token == null){
                throw new IllegalArgumentException("Token do Google inválido.");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            if(!Boolean.TRUE.equals(payload.getEmailVerified())){
                throw new IllegalArgumentException("O e-mail Google não foi verificado.");
            }

            String email = payload.getEmail().trim().toLowerCase();
            String name = payload.get("name") == null
                    ? email.substring(0,email.indexOf("@"))
                    : payload.get("name").toString();

            AppUser user = users.findByEmail(email).orElseGet(() ->
                    users.save(new AppUser(
                            name,
                            email,
                            passwordEncoder.encode(UUID.randomUUID().toString()),
                            UserRole.USER
                    ))
            );

            return new AuthResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name(),
                    jwtService.generate(user)
            );
        }catch(IllegalArgumentException e){
            throw e;
        }catch(Exception e){
            throw new IllegalStateException("Não foi possível validar a conta Google.");
        }
    }
}
