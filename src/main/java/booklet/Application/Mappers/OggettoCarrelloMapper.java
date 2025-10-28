package booklet.Application.Mappers;

import booklet.Application.DTO.OggettoCarrelloDTO;
import booklet.Application.Entities.OggettoCarrello;

import java.util.Objects;


public final class OggettoCarrelloMapper {

    private OggettoCarrelloMapper() {
    }

    public static OggettoCarrelloDTO toDto(OggettoCarrello entity) {
        Objects.requireNonNull(entity, "L'oggetto carrello da convertire non può essere nullo");
        return new OggettoCarrelloDTO(
                entity.getIsbn(),
                entity.getPrezzoUnitario(),
                entity.getQuantita()
        );
    }

    public static OggettoCarrello toEntity(OggettoCarrelloDTO dto) {
        Objects.requireNonNull(dto, "Il DTO dell'oggetto carrello non può essere nullo");
        if (dto.getQuantita() == null) {
            throw new IllegalArgumentException("La quantità dell'oggetto carrello non può essere nulla");
        }
        return new OggettoCarrello(
                Objects.requireNonNull(dto.getIsbn(), "L'id dell'oggetto carrello non può essere nullo"),
                Objects.requireNonNull(dto.getPrezzoUnitario(), "Il prezzo unitario non può essere nullo"),
                dto.getQuantita()
        );
    }
}