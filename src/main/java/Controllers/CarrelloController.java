package Controllers;

import DTO.CarrelloDTO;
import Services.CarrelloService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/me/carrello")
public class CarrelloController {

    private final CarrelloService carrelloService;

    public CarrelloController(CarrelloService carrelloService) {
        this.carrelloService = carrelloService;
    }


    @PostMapping("/items")
    public ResponseEntity<CarrelloDTO> addItem(
            @AuthenticationPrincipal Object principal,
            @Valid @RequestBody AddToCartRequest body
    ) {
        UUID userId = resolveUserId(principal);
        CarrelloDTO dto = carrelloService.aggiungiLibro(userId, body.isbn(), body.quantity());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }


    @PatchMapping("/items/{isbn}")
    public ResponseEntity<CarrelloDTO> updateQty(
            @AuthenticationPrincipal Object principal,
            @PathVariable("isbn") String isbn,
            @Valid @RequestBody UpdateQuantityRequest body
    ) {
        UUID userId = resolveUserId(principal);
        CarrelloDTO dto = carrelloService.aggiornaQuantita(userId, isbn, body.quantity());
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/items/{isbn}")
    public ResponseEntity<CarrelloDTO> removeItem(
            @AuthenticationPrincipal Object principal,
            @PathVariable("isbn") String isbn
    ) {
        UUID userId = resolveUserId(principal);
        CarrelloDTO dto = carrelloService.rimuoviLibro(userId, isbn);
        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<CarrelloDTO> getCart(@AuthenticationPrincipal Object principal) {
        UUID userId = resolveUserId(principal);
        CarrelloDTO dto = carrelloService.getCarrello(userId);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping
    public ResponseEntity<CarrelloDTO> clear(@AuthenticationPrincipal Object principal) {
        UUID userId = resolveUserId(principal);
        CarrelloDTO dto = carrelloService.svuota(userId);
        return ResponseEntity.ok(dto);
    }


    private UUID resolveUserId(Object principal) {

        try {
            String name = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            return UUID.fromString(name);
        } catch (Exception ignore) { /* fall through */ }

        throw new IllegalStateException("Impossibile risolvere lo userId dall'utente autenticato");
    }



    public record AddToCartRequest(
            @NotBlank(message = "isbn obbligatorio") String isbn,
            @Min(value = 1, message = "quantity deve essere >= 1") int quantity
    ) {}

    public record UpdateQuantityRequest(
            @Min(value = 0, message = "quantity deve essere >= 0") int quantity
    ) {}
}