package booklet.Application.Controllers;

import booklet.Application.DTO.LibroConRedazioneDTO;
import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Entities.CatalogoRedazione;
import booklet.Application.Repositories.CatalogoRedazioneRepo;
import booklet.Application.Services.CatalogoRedazioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redazione")
public class CatalogoRedazioneController {

    private final CatalogoRedazioneService service;

    private  final CatalogoRedazioneRepo redazioneRepo  ;
    public CatalogoRedazioneController(CatalogoRedazioneService service, CatalogoRedazioneRepo redazioneRepo) {
        this.service = service;
        this.redazioneRepo = redazioneRepo;
    }


    @GetMapping
    public List<LibroConRedazioneDTO> getCatalogoRedazione() {

        List<CatalogoGenerale> libri = catalogoGeneraleRepo.findAll();

        return libri.stream().map(libro -> {
            CatalogoRedazione scheda = redazioneRepo.findByLibroIsbn(libro.getIsbn())
                    .orElse(null);

            return new LibroConRedazioneDTO(
                    libro.getIsbn(),
                    libro.getTitolo(),
                    libro.getAutore(),
                    libro.getImmagineLibro(),
                    libro.getGenere(),
                    libro.getPrezzo(),

                    // parte redazione (opzionale)
                    scheda != null ? scheda.getRecensione() : null,
                    scheda != null ? scheda.getValutazioneRedazione() : null,
                    scheda != null ? scheda.getVisibile() : false
            );

        }).toList();
    }


    @PreAuthorize("hasAuthority('REDAZIONE')")
    @PostMapping("/{isbn}")
    public ResponseEntity<Void> aggiungi(@PathVariable String isbn) {
        service.aggiungiLibroARedazione(isbn);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('REDAZIONE')")
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> rimuovi(@PathVariable String isbn) {
        service.rimuoviLibroDaRedazione(isbn);
        return ResponseEntity.ok().build();
    }
}