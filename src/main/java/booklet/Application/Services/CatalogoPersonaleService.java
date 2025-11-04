package booklet.Application.Services;

import booklet.Application.DTO.CatalogoPersonaleContainerDTO;
import booklet.Application.Entities.Utente;
import booklet.Application.Mappers.CatalogoPersonaleMapper;
import booklet.Application.Repositories.CatalogoPersonaleRepo;
import booklet.Application.Repositories.UtenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogoPersonaleService {

    private final CatalogoPersonaleRepo repo;
    private final UtenteRepository utenti;

    @PreAuthorize("@ownership.check(#utenteId)")
    @Transactional(readOnly = true)
    public CatalogoPersonaleContainerDTO getCatalogo(String utenteId) {
        var righe = repo.findByUtenteId(utenteId);
        var maybeUser = utenti.findById(utenteId); // opzionale
        String username =  maybeUser.map(Utente::getUsername).orElse(null);
        return CatalogoPersonaleMapper.toContainer(utenteId, username, righe);
    }
}
