package booklet.Application.Repositories;

import booklet.Application.Entities.CatalogoRedazione;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CatalogoRedazioneRepo extends JpaRepository<CatalogoRedazione, Long> {

    boolean existsByLibroIsbn(String isbn);

    void deleteByLibroIsbn(String isbn);



}
