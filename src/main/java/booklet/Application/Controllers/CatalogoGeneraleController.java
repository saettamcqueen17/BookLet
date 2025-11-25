package booklet.Application.Controllers;

import booklet.Application.DTO.LibroDTO;
import booklet.Application.Entities.Genere;
import booklet.Application.Entities.Libro;
import booklet.Application.Services.CatalogoGeneraleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoGeneraleController {

    private final CatalogoGeneraleService service;

    public CatalogoGeneraleController(CatalogoGeneraleService service) {
        this.service = service;
    }

    // ✅ mostra solo libri con disponibilità > 0
    @GetMapping("/generale")
    public List<LibroDTO> getCatalogoGenerale() {
        return service.getCatalogoDisponibile();

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LibroDTO> aggiungi(@RequestBody LibroDTO dto) {
        LibroDTO saved = service.aggiungiLibro(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 🔥 AGGIUNGI LISTA DI LIBRI (opzionale)
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LibroDTO> aggiungiMultipli(@RequestBody List<LibroDTO> dtos) {
        return service.aggiungiLibri(dtos);
    }

    // 🔥 RIMUOVI LIBRO PER ISBN (solo ADMIN)
    @DeleteMapping("/{isbn}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rimuovi(@PathVariable String isbn) {
        service.rimuoviLibroPerIsbn(isbn);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/generi")
    public List<String> getGeneri() {
        return Arrays.stream(Genere.values())
                .map(Enum::name)
                .toList();


    }
}



