package booklet.Application.Services;

import booklet.Application.Entities.Utente;
import booklet.Application.Repositories.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UtenteService {

    private final UtenteRepository repo;

    public UtenteService(UtenteRepository repo) {
        this.repo = repo;
    }



    @Transactional
    public Utente ensureUserExistsFromJwt(Jwt jwt) {

        if (jwt == null) throw new IllegalArgumentException("JWT nullo");

        // 🔥 1) USER ID = SUB
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String username = jwt.getClaimAsString("preferred_username");

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("JWT senza 'sub'");
        }

        // 🔥 2) CERCA PER PK (utente_id)
        return repo.findById(userId)
                .orElseGet(() -> {
                    // 🔥 3) CREA UTENTE NUOVO USANDO IL SUB COME ID
                    Utente nuovo = new Utente();
                    nuovo.setUtenteId(userId);       // <-- CORRETTO
                    nuovo.setEmail(email);
                    nuovo.setUsername(username);
                    nuovo.setRuolo("USER");

                    return repo.save(nuovo);
                });
    }
}




