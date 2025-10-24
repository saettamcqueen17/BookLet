package booklet.Application.Entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "CatalogoPersonale",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id","libro_isbn"}),
        indexes = {
                @Index(name = "ix_utente_libri_utente", columnList = "utente_id"),
                @Index(name = "ix_utente_libri_scaffale", columnList = "scaffale")
        }
)
public class CatalogoPersonale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false) @JoinColumn(name = "utente_id")
    private Utente utente; //
     @ManyToOne(optional = false) @JoinColumn(name = "libro_isbn")
    private Libro libro;

    @Enumerated(EnumType.STRING)
    @Column(length = 24, nullable = false)
    private Scaffale scaffale = Scaffale.DaLeggere; // stato personale

    private Integer rating; //

    @Column(length = 4000)
    private String recensione;

    @Column(nullable = false)
    private Instant addedAt = Instant.now();

    public enum Scaffale { DaLeggere, StaiLeggendo, Finito }

    // getters/setters

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
    public Scaffale getScaffale() { return scaffale; }
    public void setScaffale(Scaffale scaffale) { this.scaffale = scaffale; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getRecensione() { return recensione; }
    public void setRecensione(String recensione) { this.recensione = recensione; }
    public Instant getAddedAt() { return addedAt; }
}