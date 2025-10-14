package booklet.Application.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "CatalogoGenerale",
        uniqueConstraints = @UniqueConstraint(columnNames = "isbn"),
        indexes = {
                @Index(name = "ix_libri_titolo", columnList = "titolo"),
                @Index(name = "ix_libri_autore", columnList = "autore"),
                @Index(name = "ix_libri_genere", columnList = "genere")
        }
)
public class CatalogoGenerale {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String isbn; // normalizza lato service (niente spazi/trattini)

    @Column(nullable = false, length = 255)
    private String titolo;

    @Column(length = 255)
    private String autore;

    @Column(length = 80)
    private String genere;

    @Column(name = "anno_pub")
    private Integer annoPubblicazione;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal prezzo = BigDecimal.ZERO;

    /** null = nessun limite hard di stock */
    private Integer disponibilita;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate void touch(){ this.updatedAt = Instant.now(); }

    // getters/setters
    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }
    public Integer getAnnoPubblicazione() { return annoPubblicazione; }
    public void setAnnoPubblicazione(Integer annoPubblicazione) { this.annoPubblicazione = annoPubblicazione; }
    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }
    public Integer getDisponibilita() { return disponibilita; }
    public void setDisponibilita(Integer disponibilita) { this.disponibilita = disponibilita; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}