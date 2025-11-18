package booklet.Application.DTO;

import java.math.BigDecimal;

public record LibroConRedazioneDTO(
        String isbn,
        String titolo,
        String autore,
        String immagineLibro,
        String genere,
        BigDecimal prezzo,

        // parte redazione
        String recensione,
        Double valutazioneRedazione,
        Boolean visibile
) {}

