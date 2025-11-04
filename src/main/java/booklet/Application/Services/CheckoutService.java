package booklet.Application.Services;

import booklet.Application.Repositories.CatalogoGeneraleRepo;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutService {


    private final CarrelloService carrelli;            // in-memory
    private final CatalogoGeneraleRepo catalogoGenerale;

    @PreAuthorize("@ownership.check(#utenteId)")
    @org.springframework.transaction.annotation.Transactional
    public void checkout(String utenteId) {
        var cart = carrelli.getCarrello(utenteId);
        if (cart.getOggetti().isEmpty()) {
            throw new IllegalStateException("Carrello vuoto");
        }

        for (var riga : cart.getOggetti()) {
            int rows = catalogoGenerale.decrementaDisponibilitaSeSufficiente(riga.getIsbn(), riga.getQuantita());
            if (rows == 0) {
                throw new IllegalStateException("Disponibilità insufficiente per ISBN " + riga.getIsbn());
            }
        }



        carrelli.svuota(utenteId);
    }
}