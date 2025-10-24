package booklet.Application.Services;

import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Entities.CatalogoRedazione;
import booklet.Application.Entities.Genere;
import booklet.Application.Entities.Libro;
import booklet.Application.Repositories.CatalogoGeneraleRepo;
import booklet.Application.Repositories.CatalogoRedazioneRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogoRedazioneService {

    private final CatalogoRedazioneRepo redazioneRepo;
    private final CatalogoGeneraleRepo generaleRepo;

    public CatalogoRedazioneService(CatalogoRedazioneRepo redazioneRepo,
                                    CatalogoGeneraleRepo generaleRepo) {
        this.redazioneRepo = redazioneRepo;
        this.generaleRepo = generaleRepo;
    }

    @Transactional
    public CatalogoRedazione aggiungiScheda(String isbn, Genere genere, String recensione, Double voto) {
        CatalogoGenerale libro = generaleRepo.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato"));

        CatalogoRedazione scheda = new CatalogoRedazione();
        scheda.setLibro(libro);
        scheda.setGenere(genere);
        scheda.setRecensione(recensione);
        scheda.setValutazioneRedazione(voto);
        scheda.setVisibile(true);

        return redazioneRepo.save(scheda);
    }

    public void rimuoviScheda(String isbn) {
        redazioneRepo.deleteById(isbn);
    }

    public List<CatalogoRedazione> getTutti() {
        return redazioneRepo.findAll();
    }

    public List<CatalogoRedazione> getVisibili() {
        return redazioneRepo.findByVisibileTrue();
    }


    @Transactional
    public CatalogoRedazione aggiungiLibroARedazione(String isbn, Genere genere,
                                                     String recensione, Double voto) {

        CatalogoGenerale libro = generaleRepo.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato nel catalogo generale."));


        CatalogoRedazione scheda = redazioneRepo.findById(isbn)
                .orElse(new CatalogoRedazione());
        scheda.setLibro(libro);
        scheda.setIsbn(libro.getIsbn());
        scheda.setGenere(genere);
        scheda.setRecensione(recensione);
        scheda.setValutazioneRedazione(voto);
        scheda.setVisibile(true);

        return redazioneRepo.save(scheda);
    }


    @Transactional
    public void rimuoviLibroDaRedazione(String isbn) {
        if (!redazioneRepo.existsById(isbn)) {
            throw new IllegalArgumentException("Nessuna scheda redazionale trovata per ISBN: " + isbn);
        }
        redazioneRepo.deleteById(isbn);
    }
}