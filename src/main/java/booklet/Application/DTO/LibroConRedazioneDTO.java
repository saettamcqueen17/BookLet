package booklet.Application.DTO;

import booklet.Application.Entities.Genere;

import java.math.BigDecimal;

public record LibroConRedazioneDTO(
        String isbn,
        String titolo,
        String autore,
        String immagineLibro,
        Genere genere,
        BigDecimal prezzo,

        // parte redazione
        String recensione,
        Double valutazioneRedazione,
        Boolean visibile
) {}

