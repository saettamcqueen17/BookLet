package booklet.Application.DTO;

import booklet.Application.Entities.Genere;

import java.math.BigDecimal;


public class LibroDTO {


    private String isbn;
    private String titolo;
    private String autore;
    private String casaEditrice;
    private Genere genere;
    private BigDecimal prezzo;
    private Integer disponibilita;

    private String immagineLibro ;
    public LibroDTO() {
    }

    public LibroDTO(
                    String isbn,
                    String titolo,
                    String autore,
                    String casaEditrice,
                    String immagineLibro,
                    Genere genere,
                    BigDecimal prezzo,
                    Integer disponibilita) {

        this.isbn = isbn;
        this.titolo = titolo;
        this.immagineLibro = immagineLibro ;
        this.autore = autore;
        this.casaEditrice = casaEditrice;
        this.genere = genere;
        this.prezzo = prezzo;
        this.disponibilita = disponibilita;
    }





    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitolo() {
        return titolo;
    }


    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getImmagineLibro(){
        return immagineLibro;
    }

    public void setImmagineLibro(String immagineLibro) {
        this.immagineLibro = immagineLibro;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getCasaEditrice() {
        return casaEditrice;
    }

    public void setCasaEditrice(String casaEditrice) {
        this.casaEditrice = casaEditrice;
    }

    public Genere getGenere() {
        return genere;
    }

    public void setGenere(Genere genere) {
        this.genere = genere;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public Integer getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(Integer disponibilita) {
        this.disponibilita = disponibilita;
    }
}