package booklet.Application.Services;

import booklet.Application.DTO.CatalogoPersonaleContainerDTO;
import booklet.Application.DTO.CatalogoPersonaleDTO;
import booklet.Application.Entities.CatalogoPersonale;
import booklet.Application.Entities.Utente;
import booklet.Application.Mappers.CatalogoPersonaleMapper;
import booklet.Application.Repositories.CatalogoPersonaleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

@Service
@RequiredArgsConstructor
public class CatalogoPersonaleService {

    private final CatalogoPersonaleRepo repo;
    private final UtenteService utenteService;

    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public CatalogoPersonaleContainerDTO getCatalogo(Object principal) {
        Utente utente = utenteService.ensureUserExistsFromJwt((Jwt) principal);

        var righe = repo.findByUtenteId(utente.getUtenteId());

        return CatalogoPersonaleMapper.toContainer(
                utente.getUtenteId(),
                utente.getUsername(),
                righe
        );
    }


    @Transactional
    public CatalogoPersonaleDTO aggiornaScaffale(Object principal, String isbn,
                                                 CatalogoPersonale.Scaffale scaffale) {

        Utente utente = utenteService.ensureUserExistsFromJwt((Jwt) principal);
        repo.aggiornaScaffale(utente.getUtenteId(), isbn, scaffale);

        // ritorno riga aggiornata
        var updated = repo.findByUtenteIdAndLibroIsbn(utente.getUtenteId(), isbn)
                .orElseThrow();
        return CatalogoPersonaleMapper.toDto(updated);
    }

    @Transactional
    public CatalogoPersonaleDTO aggiornaRecensione(Object principal, String isbn, String recensione) {

        Utente utente = utenteService.ensureUserExistsFromJwt((Jwt) principal);

        repo.aggiornaValutazioneERecensione(utente.getUtenteId(), isbn, null, recensione);

        var updated = repo.findByUtenteIdAndLibroIsbn(utente.getUtenteId(), isbn)
                .orElseThrow();

        return CatalogoPersonaleMapper.toDto(updated);
    }
}




