package booklet.Application.Mappers;

import booklet.Application.DTO.CarrelloDTO;
import booklet.Application.DTO.OggettoCarrelloDTO;
import booklet.Application.Entities.Carrello;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public final class CarrelloMapper {

    private CarrelloMapper() {
    }

    public static CarrelloDTO toDto(Carrello carrello) {
        Objects.requireNonNull(carrello, "Il carrello da convertire non può essere nullo");

        List<OggettoCarrelloDTO> oggetti = carrello.getLibriNelCarrello().stream()
                .map(OggettoCarrelloMapper::toDto)
                .collect(Collectors.toList());

        CarrelloDTO dto = new CarrelloDTO();
        dto.setOggetti(oggetti);
        dto.setNumeroTotaleOggetti(carrello.getNumeroTotaleOggetti());
        dto.setTotale(carrello.calcolaTotale());
        return dto;
    }

    public static Carrello toEntity(CarrelloDTO dto) {
        Objects.requireNonNull(dto, "Il DTO del carrello non può essere nullo");

        Carrello carrello = new Carrello();
        dto.getOggetti().forEach(oggetto -> carrello.aggiungiOggetto(OggettoCarrelloMapper.toEntity(oggetto)));
        return carrello;
    }
}
