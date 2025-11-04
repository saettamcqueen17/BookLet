package booklet.Application.DTO;

import java.util.List;
import java.util.UUID;

public record CatalogoPersonaleContainerDTO(
        String utenteId,
        String username,        // opzionale: o null se non vuoi esporlo
        int totaleLibri,
        List<CatalogoPersonaleDTO> libri
) {}
