package booklet.Application.Controllers;

import booklet.Application.DTO.CatalogoPersonaleContainerDTO;
import booklet.Application.Services.CatalogoPersonaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

    @RestController
    @RequestMapping("/api/catalogo-personale")
    @RequiredArgsConstructor
    public class CatalogoPersonaleController {

        private final CatalogoPersonaleService service;

        @GetMapping("/{utenteId}")
        public CatalogoPersonaleContainerDTO get(@PathVariable UUID utenteId) {
            return service.getCatalogo(utenteId);
        }
    }

