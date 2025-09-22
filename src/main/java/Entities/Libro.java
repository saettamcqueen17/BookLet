package Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id autoincrementale
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String titolo;

    @Column(nullable = false)
    private String autore;

    private String casaEditrice;

    @Enumerated(EnumType.STRING)
    private Genere genere;

    @Column(nullable = false)
    private BigDecimal prezzo;

    @Column(nullable = false)
    private Integer disponibilita;


    protected Libro() {
    }


    public Libro(String isbn, String titolo, String autore, String casaEditrice,
                 Genere genere, BigDecimal prezzo, Integer disponibilita) {
        this.isbn = isbn;
        this.titolo = titolo;
        this.autore = autore;
        this.casaEditrice = casaEditrice;
        this.genere = genere;
        this.prezzo = prezzo;
        this.disponibilita = disponibilita;
    }

    // getter e setter
    public Long getId() {
        return id;
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
