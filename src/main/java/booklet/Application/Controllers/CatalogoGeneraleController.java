package booklet.Application.Controllers;

import booklet.Application.Entities.Libro;
import booklet.Application.Services.CatalogoGeneraleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "*")
public class CatalogoGeneraleController {

    private final CatalogoGeneraleService service;

    public CatalogoGeneraleController(CatalogoGeneraleService service) {
        this.service = service;
    }

    // ✅ mostra solo libri con disponibilità > 0
    @GetMapping("/generale")
    public List<Libro> getCatalogoGenerale() {
        return service.getCatalogoDisponibile();
    }
}