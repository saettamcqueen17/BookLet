package booklet.Application.Services;

import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Repositories.CatalogoGeneraleRepo;
import booklet.Application.Repositories.LibroRepository;
import booklet.Application.Services.CarrelloService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CarrelloService carrelli;
    private final CatalogoGeneraleRepo catalogoGenerale;


    @PreAuthorize("@ownership.check(#utenteId)")
    @Transactional
    public void checkout(UUID utenteId) {
        var cartDto = carrelli.getCarrello(utenteId);   // legge lo stato in-memory
        if (cartDto.getOggetti().isEmpty()) throw new IllegalStateException("Carrello vuoto");


        for (var riga : cartDto.getOggetti()) {
            int rows = catalogoGenerale.decrementaDisponibilitaSeSufficiente(riga.getIsbn(), riga.getQuantita());
            if (rows == 0) {
                // rollback automatico della @Transactional
                throw new IllegalStateException("Disponibilità insufficiente per ISBN " + riga.getIsbn());
            }
        }


        carrelli.svuota(utenteId);

    }
}
