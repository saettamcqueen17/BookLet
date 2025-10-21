package booklet.Application.Repositories;



import booklet.Application.Entities.CatalogoRedazione;
import booklet.Application.Entities.Genere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CatalogoRedazioneRepo extends JpaRepository<CatalogoRedazione, String> {
    List<CatalogoRedazione> findByGenere(Genere genere) ;
    List<CatalogoRedazione> findByVisibileTrue();
}
