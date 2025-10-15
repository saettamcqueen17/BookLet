package booklet.Application.DTO;

import booklet.Application.Entities.CatalogoPersonale;

import java.time.Instant;

public record CatalogoPersonaleDTO(

        String titolo,
        String isbn,
        String autore,
        CatalogoPersonale.Scaffale scaffale,
        Integer rating,
        String recensione,
        Instant addedAt
) {}
