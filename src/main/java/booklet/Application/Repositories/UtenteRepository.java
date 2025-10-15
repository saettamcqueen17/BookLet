package booklet.Application.Repositories;



import booklet.Application.Entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository extends JpaRepository<Utente, UUID> {

    Optional<Utente> findByEmail(String email);

    Optional<Utente> findByUsername(String username);
}
