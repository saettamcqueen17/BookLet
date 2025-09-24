package Mappers;

import DTO.LibroDTO;
import Entities.Libro;

import java.lang.reflect.Field;
import java.util.Objects;


public final class LibroMapper {

    private LibroMapper() {
    }

    public static LibroDTO toDto(Libro libro) {
        Objects.requireNonNull(libro, "Il libro da convertire non può essere nullo");
        LibroDTO dto = new LibroDTO();
        dto.setId(libro.getId());
        dto.setIsbn(libro.getIsbn());
        dto.setTitolo(libro.getTitolo());
        dto.setAutore(libro.getAutore());
        dto.setCasaEditrice(libro.getCasaEditrice());
        dto.setGenere(libro.getGenere());
        dto.setPrezzo(libro.getPrezzo());
        dto.setDisponibilita(libro.getDisponibilita());
        return dto;
    }

    public static Libro toEntity(LibroDTO dto) {
        Objects.requireNonNull(dto, "Il DTO del libro non può essere nullo");

        Libro libro = new Libro(
                Objects.requireNonNull(dto.getIsbn(), "L'ISBN del libro non può essere nullo"),
                Objects.requireNonNull(dto.getTitolo(), "Il titolo del libro non può essere nullo"),
                Objects.requireNonNull(dto.getAutore(), "L'autore del libro non può essere nullo"),
                dto.getCasaEditrice(),
                dto.getGenere(),
                Objects.requireNonNull(dto.getPrezzo(), "Il prezzo del libro non può essere nullo"),
                Objects.requireNonNull(dto.getDisponibilita(), "La disponibilità del libro non può essere nulla")
        );

        impostaIdSePresente(libro, dto.getId());
        return libro;
    }

    private static void impostaIdSePresente(Libro libro, Long id) {
        if (id == null) {
            return;
        }
        try {
            Field idField = Libro.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(libro, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Impossibile impostare l'id sul libro", e);
        }
    }
}
