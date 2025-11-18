package booklet.Application.Services;

import booklet.Application.DTO.LibroDTO;
import booklet.Application.Entities.CatalogoRedazione;
import booklet.Application.Entities.Libro;
import booklet.Application.Mappers.LibroMapper;
import booklet.Application.Repositories.CatalogoRedazioneRepo;
import booklet.Application.Repositories.LibroRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogoRedazioneService {

    private final CatalogoRedazioneRepo redazioneRepo;
    private final LibroRepo libroRepo;

    public CatalogoRedazioneService(CatalogoRedazioneRepo redazioneRepo, LibroRepo libroRepo) {
        this.redazioneRepo = redazioneRepo;
        this.libroRepo = libroRepo;
    }

    /**
     * Restituisce il catalogo della redazione
     * (lista di LibroDTO, ottenuti da Libro tramite LibroMapper).
     */
    @Transactional(readOnly = true)
    public List<LibroDTO> getCatalogoRedazione() {
        return redazioneRepo.findAll()
                .stream()
                .map(CatalogoRedazione::getLibro)
                .map(LibroMapper::toDto)
                .collect(Collectors.toList());
    }


    public void aggiungiLibroARedazione(String isbn) {
        // se è già presente, non facciamo nulla (puoi anche lanciare eccezione se preferisci)
        if (redazioneRepo.existsByLibroIsbn(isbn)) {
            return;
        }

        Libro libro = libroRepo.findById(isbn)
                .orElseThrow(() -> new EntityNotFoundException("Libro non trovato: " + isbn));

        CatalogoRedazione r = new CatalogoRedazione(libro);
        redazioneRepo.save(r);
    }


    public void rimuoviLibroDaRedazione(String isbn) {
        redazioneRepo.deleteByLibroIsbn(isbn);
    }
}