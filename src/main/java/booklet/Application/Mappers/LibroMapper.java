package booklet.Application.Mappers;

import booklet.Application.DTO.LibroDTO;
import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Entities.Libro;

public class LibroMapper {

    /* ==================== MAPPATURA da CATALOGO GENERALE ==================== */

    public static LibroDTO toDto(CatalogoGenerale entity) {
        LibroDTO dto = new LibroDTO();
        dto.setIsbn(entity.getIsbn());
        dto.setTitolo(entity.getTitolo());
        dto.setAutore(entity.getAutore());
        dto.setCasaEditrice(entity.getCasa_editrice());
        dto.setGenere(entity.getGenere());
        dto.setPrezzo(entity.getPrezzo());
        dto.setDisponibilita(entity.getDisponibilita());
        dto.setImmagineLibro(entity.getImmagineLibro());
        return dto;
    }

    public static CatalogoGenerale toCatalogoGenerale(LibroDTO dto) {
        CatalogoGenerale c = new CatalogoGenerale();
        c.setIsbn(dto.getIsbn());
        c.setTitolo(dto.getTitolo());
        c.setAutore(dto.getAutore());
        c.setCasa_editrice(dto.getCasaEditrice());
        c.setGenere(dto.getGenere());
        c.setPrezzo(dto.getPrezzo());
        c.setDisponibilita(dto.getDisponibilita());
        c.setImmagineLibro(dto.getImmagineLibro());
        return c;
    }

    /* ==================== MAPPATURA da LIBRO ==================== */

    public static LibroDTO toDto(Libro entity) {
        LibroDTO dto = new LibroDTO();
        dto.setIsbn(entity.getIsbn());
        dto.setTitolo(entity.getTitolo());
        dto.setAutore(entity.getAutore());
        dto.setCasaEditrice(entity.getCasaEditrice());
        dto.setGenere(entity.getGenere());
        dto.setPrezzo(entity.getPrezzo());
        dto.setDisponibilita(entity.getDisponibilita());
        dto.setImmagineLibro(entity.getImmagineLibro());
        return dto;
    }

    public static Libro toLibro(LibroDTO dto) {
        Libro l = new Libro();
        l.setIsbn(dto.getIsbn());
        l.setTitolo(dto.getTitolo());
        l.setAutore(dto.getAutore());
        l.setCasaEditrice(dto.getCasaEditrice());
        l.setGenere(dto.getGenere());
        l.setPrezzo(dto.getPrezzo());
        l.setDisponibilita(dto.getDisponibilita());
        l.setImmagineLibro(dto.getImmagineLibro());
        return l;
    }
}