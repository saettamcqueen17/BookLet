package booklet.Application.Entities;

import booklet.Application.Entities.Libro;
import booklet.Application.Entities.Utente;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(
        name = "catalogo_personale",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id","libro_isbn"}),
        indexes = {
                @Index(name = "ix_utente_libri_utente", columnList = "utente_id"),
                @Index(name = "ix_utente_libri_scaffale", columnList = "scaffale")
        }
)
public class CatalogoPersonale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;       // <-- DEVE ESSERE LONG, NON STRING

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id", nullable = false)
    private Utente utente;    // <-- UNICA MAPPATURA di utente_id

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_isbn", nullable = false)
    private Libro libro;

    @Enumerated(EnumType.STRING)
    @Column(length = 24, nullable = false)
    private Scaffale scaffale = Scaffale.DaLeggere;

    private Integer rating;

    @Column(length = 4000)
    private String recensione;

    @Column(nullable = false)
    private Instant addedAt = Instant.now();

    public enum Scaffale { DaLeggere, StaiLeggendo, Finito }


}
