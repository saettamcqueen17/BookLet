package Mappers;

import DTO.OggettoCarrelloDTO;
import Entities.OggettoCarrello;

import java.util.Objects;


public final class OggettoCarrelloMapper {

    private OggettoCarrelloMapper() {
    }

    public static OggettoCarrelloDTO toDto(OggettoCarrello entity) {
        Objects.requireNonNull(entity, "L'oggetto carrello da convertire non può essere nullo");
        return new OggettoCarrelloDTO(
                entity.getId(),
                entity.getNome(),
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
                Objects.requireNonNull(dto.getId(), "L'id dell'oggetto carrello non può essere nullo"),
                Objects.requireNonNull(dto.getNome(), "Il nome dell'oggetto carrello non può essere nullo"),
                Objects.requireNonNull(dto.getPrezzoUnitario(), "Il prezzo unitario non può essere nullo"),
                dto.getQuantita()
        );
    }
}