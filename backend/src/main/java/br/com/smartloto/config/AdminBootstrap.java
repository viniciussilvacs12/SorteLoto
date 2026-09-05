package br.com.smartloto.config;

import br.com.smartloto.domain.AppUser;
import br.com.smartloto.domain.UserRole;
import br.com.smartloto.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final AppUserRepository repository;
    private final PasswordEncoder encoder;

    @Value("${smartloto.admin.enabled:false}")
    private boolean enabled;

    @Value("${smartloto.admin.sync-on-start:false}")
    private boolean syncOnStart;

    @Value("${smartloto.admin.email:admin@smartloto.local}")
    private String email;

    @Value("${smartloto.admin.password:}")
    private String password;

    public AdminBootstrap(AppUserRepository repository, PasswordEncoder encoder){
        this.repository=repository;
        this.encoder=encoder;
    }

    @Override
    @Transactional
    public void run(String... args){
        if(!enabled) return;
        if(password == null || password.isBlank())
            throw new IllegalStateException("SMARTLOTO_ADMIN_PASSWORD deve ser definida quando o admin bootstrap estiver habilitado.");

        String normalizedEmail = email.trim().toLowerCase();

        var existing = repository.findByEmail(normalizedEmail);
        if(existing.isEmpty()){
            repository.save(new AppUser(
                    "Administrador SmartLoto",
                    normalizedEmail,
                    encoder.encode(password),
                    UserRole.ADMIN
            ));
            return;
        }

        // Desenvolvimento: corrige automaticamente credenciais antigas (v0.6 SHA-256)
        // e garante role ADMIN. Pode ser desativado em produção.
        if(syncOnStart){
            AppUser admin = existing.get();
            admin.synchronizeAdminCredentials(
                    "Administrador SmartLoto",
                    encoder.encode(password)
            );
            repository.save(admin);
        }
    }
}
