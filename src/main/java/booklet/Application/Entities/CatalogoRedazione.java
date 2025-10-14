package booklet.Application.Entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "scelte_redazione",
        uniqueConstraints = @UniqueConstraint(columnNames = "libro_id"),
        indexes = @Index(name = "ix_scelte_redazione_rank", columnList = "posizione")
)
public class CatalogoRedazione{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "libro_id")
    private Libro libro;


    @Column(name = "posizione")
    private Integer posizione;


    @Column(length = 1000)
    private String descrizione;


    private Instant attivaDa;
    private Instant attivaFinoA;

    private Boolean pubblicata = Boolean.TRUE;

    // getters/setters
    public Long getId() { return id; }
    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
    public Integer getPosizione() { return posizione; }
    public void setPosizione(Integer posizione) { this.posizione = posizione; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public Instant getAttivaDa() { return attivaDa; }
    public void setAttivaDa(Instant attivaDa) { this.attivaDa = attivaDa; }
    public Instant getAttivaFinoA() { return attivaFinoA; }
    public void setAttivaFinoA(Instant attivaFinoA) { this.attivaFinoA = attivaFinoA; }
    public Boolean getPubblicata() { return pubblicata; }
    public void setPubblicata(Boolean pubblicata) { this.pubblicata = pubblicata; }
}