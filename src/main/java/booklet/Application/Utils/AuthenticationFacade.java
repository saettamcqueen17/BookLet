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

/**
 * Classe di utilità per accedere all'utente autenticato in base ai dati del token JWT.
 * Può restituire direttamente la tua entity Utente dal database oppure le informazioni base.
 */
@Component
public class AuthenticationFacade {

    private final UtenteRepository utenteRepository;

    public AuthenticationFacade(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    /**
     * Restituisce l'entity Utente dal database in base all'email contenuta nel token JWT.
     * Lancia eccezione se il token non è valido o l'utente non esiste nel DB.
     */
    public Utente getUtenteAutenticatoOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Utente non autenticato o token non valido");
        }

        String email = jwt.getClaimAsString("email");
        if (email == null) {
            throw new IllegalStateException("Il token JWT non contiene il claim 'email'");
        }

        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utente non trovato nel DB: " + email));
    }

    /**
     * Restituisce solo info base sull'utente autenticato (senza query al DB).
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