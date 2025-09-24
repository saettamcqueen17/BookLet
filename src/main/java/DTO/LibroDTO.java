package DTO;

import Entities.Genere;

import java.math.BigDecimal;


public class LibroDTO {

    private Long id;
    private String isbn;
    private String titolo;
    private String autore;
    private String casaEditrice;
    private Genere genere;
    private BigDecimal prezzo;
    private Integer disponibilita;

    public LibroDTO() {
    }

    public LibroDTO(Long id,
                    String isbn,
                    String titolo,
                    String autore,
                    String casaEditrice,
                    Genere genere,
                    BigDecimal prezzo,
                    Integer disponibilita) {
        this.id = id;
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.casaEditrice = casaEditrice;
        this.genere = genere;
        this.prezzo = prezzo;
        this.disponibilita = disponibilita;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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