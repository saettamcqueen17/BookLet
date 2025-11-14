package booklet.Application.Repositories;


import booklet.Application.Entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, String> {

    Optional<Utente> findByUtenteId(String utenteId);

    Optional<Utente> findByUsername(String username);

    Optional <Utente> findByEmail(String email);
}