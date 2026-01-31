package booklet.Application.Controllers;

import booklet.Application.DTO.CarrelloDTO;
import booklet.Application.Services.CarrelloService;
import booklet.Application.Services.UtenteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/me/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;
    private final UtenteService utenteService;

    public CarrelloController(CarrelloService carrelloService, UtenteService utenteService) {
        this.carrelloService = carrelloService;
        this.utenteService = utenteService;
    }

    @PostMapping("/items")
    public ResponseEntity<CarrelloDTO> addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AddToCartRequest body
    ) {
        String userId = jwt.getSubject();      // <-- SUB
        utenteService.ensureUserExistsFromJwt(jwt);     // <-- FIX FONDAMENTALE!!
        CarrelloDTO dto = carrelloService.aggiungiLibro(userId, body.isbn(), body.quantita());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PatchMapping("/items/{isbn}")
    public ResponseEntity<CarrelloDTO> updateQty(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("isbn") String isbn,
            @Valid @RequestBody CarrelloController.UpdateQuantitaRequest body
    ) {
        String userId = jwt.getSubject();      // <-- SUB
        utenteService.ensureUserExistsFromJwt(jwt);     // <-- FIX FONDAMENTALE!!
        CarrelloDTO dto = carrelloService.aggiornaQuantita(userId, isbn, body.quantita());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/items/{isbn}")
    public ResponseEntity<CarrelloDTO> removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("isbn") String isbn
    ) {
        String userId = jwt.getClaimAsString("sub");
        CarrelloDTO dto = carrelloService.rimuoviLibro(userId, isbn);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<CarrelloDTO> getCart(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();      // <-- SUB
        utenteService.ensureUserExistsFromJwt(jwt);     // <-- FIX FONDAMENTALE!!
        CarrelloDTO dto = carrelloService.getCarrello(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CarrelloDTO carrelloDTO
    ) {

        String userId = jwt.getSubject();
        utenteService.ensureUserExistsFromJwt(jwt);

        carrelloService.checkout(userId,carrelloDTO);
        return ResponseEntity.ok("Checkout completato con successo!");
    }
    // ➤ Svuota completamente il carrello
    @DeleteMapping
    public ResponseEntity<CarrelloDTO> clear(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        utenteService.ensureUserExistsFromJwt(jwt) ;
        CarrelloDTO dto = carrelloService.svuota(userId);
        return ResponseEntity.ok(dto);
    }

    public record AddToCartRequest(
            @NotBlank(message = "isbn obbligatorio") String isbn,
            @Min(value = 1, message = "quantity deve essere >= 1") int quantita
    ) {}

    public record UpdateQuantitaRequest(
            @Min(value = 0, message = "quantity deve essere >= 0") int quantita
    ) {}
}