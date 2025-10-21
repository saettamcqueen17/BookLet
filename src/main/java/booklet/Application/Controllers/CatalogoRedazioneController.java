package booklet.Application.Controllers;



import booklet.Application.Entities.CatalogoRedazione;
import booklet.Application.Entities.Genere;
import booklet.Application.Services.CatalogoRedazioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/redazione")
public class CatalogoRedazioneController {

    private final CatalogoRedazioneService service;

    public CatalogoRedazioneController(CatalogoRedazioneService service) {
        this.service = service;
    }

    @GetMapping
    public List<CatalogoRedazione> tutti() {
        return service.getTutti();
    }

    @GetMapping("/visibili")
    public List<CatalogoRedazione> visibili() {
        return service.getVisibili();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/aggiungi")
    public ResponseEntity<CatalogoRedazione> aggiungi(
            @RequestParam String isbn,
            @RequestParam Genere genere,
            @RequestParam(required = false) String recensione,
            @RequestParam(required = false) Double voto) {
        return ResponseEntity.ok(service.aggiungiLibroARedazione(isbn, genere, recensione, voto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> rimuovi(@PathVariable String isbn) {
        service.rimuoviLibroDaRedazione(isbn);
        return ResponseEntity.noContent().build();
    }
}
