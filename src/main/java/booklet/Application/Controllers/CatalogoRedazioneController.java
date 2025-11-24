package booklet.Application.Controllers;

import booklet.Application.DTO.LibroConRedazioneDTO;
import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Entities.CatalogoRedazione;
import booklet.Application.Repositories.CatalogoGeneraleRepo;
import booklet.Application.Repositories.CatalogoRedazioneRepo;
import booklet.Application.Services.CatalogoRedazioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/redazione")
public class CatalogoRedazioneController {

    private final CatalogoRedazioneService service;
    private final CatalogoGeneraleRepo catalogoGeneraleRepo;

    private  final CatalogoRedazioneRepo redazioneRepo  ;
    public CatalogoRedazioneController(CatalogoRedazioneService service, CatalogoGeneraleRepo catalogoGeneraleRepo, CatalogoRedazioneRepo redazioneRepo) {
        this.service = service;
        this.catalogoGeneraleRepo = catalogoGeneraleRepo;
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

                    scheda != null ? scheda.getRecensione() : null,
                    scheda != null ? scheda.getValutazioneRedazione() : null,
                    scheda != null ? scheda.getVisibile() : false
            );

        }).toList();
    }

    @PreAuthorize("hasRole('REDAZIONE')")
    @PutMapping("/{isbn}/visibile")
    public ResponseEntity<Void> cambiaVisibile(
            @PathVariable String isbn,
            @RequestBody Map<String, Boolean> body
    ) {
        service.cambiaVisibile(isbn, body.get("visibile"));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('REDAZIONE')")
    @PutMapping("/{isbn}/recensione")
    public ResponseEntity<Void> modificaRecensione(
            @PathVariable String isbn,
            @RequestBody RecensioneRequest req
    ) {
        service.modificaRecensione(isbn, req.recensione(), req.valutazione());
        return ResponseEntity.ok().build();
    }

    public record RecensioneRequest(String recensione, Double valutazione) {}


    @PreAuthorize("hasRole('REDAZIONE')")
    @PostMapping("/{isbn}")
    public ResponseEntity<Void> aggiungi(@PathVariable String isbn) {
        service.aggiungiLibroARedazione(isbn);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('REDAZIONE')")
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> rimuovi(@PathVariable String isbn) {
        service.rimuoviLibroDaRedazione(isbn);
        return ResponseEntity.ok().build();
    }
}