package br.com.smartloto.security;

import br.com.smartloto.domain.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(
            @Value("${smartloto.jwt.secret}") String secret,
            @Value("${smartloto.jwt.expiration-minutes:120}") long expirationMinutes
    ){
        if(secret == null || secret.length() < 32)
            throw new IllegalStateException("smartloto.jwt.secret deve ter pelo menos 32 caracteres.");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(AppUser user){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public String extractRole(String token){
        Object role = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().get("role");
        return role == null ? "USER" : role.toString();
    }

    public boolean isValid(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
