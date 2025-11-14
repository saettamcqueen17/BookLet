package booklet.Application.Controllers;

import booklet.Application.DTO.CatalogoPersonaleContainerDTO;
import booklet.Application.DTO.CatalogoPersonaleDTO;
import booklet.Application.Entities.CatalogoPersonale;
import booklet.Application.Services.CatalogoPersonaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me/catalogo-personale")
@RequiredArgsConstructor
public class CatalogoPersonaleController {

    private final CatalogoPersonaleService catalogoService;

    @GetMapping


    public ResponseEntity<CatalogoPersonaleContainerDTO> get(@AuthenticationPrincipal Jwt jwt) {
        var catalogo = catalogoService.getCatalogo(jwt);
        return ResponseEntity.ok(catalogo);
    }

    @PutMapping("/{isbn}/scaffale")
    public ResponseEntity<CatalogoPersonaleDTO> aggiornaScaffale(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String isbn,
            @RequestBody ScaffaleRequest body) {

        var result = catalogoService.aggiornaScaffale(jwt, isbn, body.scaffale());
        return ResponseEntity.ok(result);
    }

    // PUT modifica recensione
    @PutMapping("/{isbn}/recensione")
    public ResponseEntity<CatalogoPersonaleDTO> aggiornaRecensione(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String isbn,
            @RequestBody RecensioneRequest body) {

        var result = catalogoService.aggiornaRecensione(jwt, isbn, body.recensione());
        return ResponseEntity.ok(result);
    }

    // ====== RICHIESTE JSON ======
    public record ScaffaleRequest(CatalogoPersonale.Scaffale scaffale) {}
    public record RecensioneRequest(String recensione) {}
}






