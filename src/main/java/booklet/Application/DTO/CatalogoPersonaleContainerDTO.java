package booklet.Application.DTO;

import java.util.List;
import java.util.UUID;

public record CatalogoPersonaleContainerDTO(
        UUID utenteId,
        String username,        // opzionale: o null se non vuoi esporlo
        int totaleLibri,
        List<CatalogoPersonaleDTO> libri
) {}
