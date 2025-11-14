package booklet.Application.Utils;

import booklet.Application.Entities.Utente;
import booklet.Application.Repositories.UtenteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthenticationFacade {

    private final UtenteRepository utenteRepository;

    public AuthenticationFacade(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }


    public Utente getUtenteAutenticatoOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Utente non autenticato o token non valido");
        }

        // 🔥 USER ID = SUB
        String userId = jwt.getSubject();  // <-- ECCO IL CAMBIAMENTO FONDAMENTALE

        return utenteRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Utente non trovato nel DB (sub): " + userId));
    }

    /**
     * Informazioni leggere, utili per il front-end.
     */
    public SimpleUserInfo getSimpleInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return new SimpleUserInfo(username, email, roles);
    }

    public record SimpleUserInfo(String username, String email, Set<String> roles) {}
}
