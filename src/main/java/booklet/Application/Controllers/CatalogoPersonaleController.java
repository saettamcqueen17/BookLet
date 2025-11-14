package booklet.Application.Controllers;

import booklet.Application.DTO.CatalogoPersonaleContainerDTO;
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
}



