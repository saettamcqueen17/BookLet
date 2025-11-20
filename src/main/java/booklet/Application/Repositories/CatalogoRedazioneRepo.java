package booklet.Application.Repositories;

import booklet.Application.Entities.CatalogoRedazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CatalogoRedazioneRepo extends JpaRepository<CatalogoRedazione, Long> {

    boolean existsByLibroIsbn(String isbn);

    void deleteByLibroIsbn(String isbn);

    Optional<CatalogoRedazione> findByLibroIsbn(String isbn);


}
