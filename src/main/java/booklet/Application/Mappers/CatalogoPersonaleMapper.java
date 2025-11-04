package booklet.Application.Mappers;

import booklet.Application.DTO.CatalogoPersonaleContainerDTO;
import booklet.Application.DTO.CatalogoPersonaleDTO;
import booklet.Application.Entities.CatalogoPersonale;

import java.util.List;

public class CatalogoPersonaleMapper {

    public static CatalogoPersonaleDTO toDto(CatalogoPersonale c) {
        var l = c.getLibro();
        return new CatalogoPersonaleDTO(
                l.getTitolo(),
                l.getIsbn(),
                l.getAutore(),
                c.getScaffale(),
                c.getRating(),
                c.getRecensione(),
                c.getAddedAt()
        );
    }

    public static CatalogoPersonaleContainerDTO toContainer(
            String utenteId,
            String username, // opzionale
            List<CatalogoPersonale> righe) {

        var items = righe.stream().map(CatalogoPersonaleMapper::toDto).toList();
        return new CatalogoPersonaleContainerDTO(
                utenteId,
                username,
                items.size(),
                items
        );
    }
}