package booklet.Application.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_redazione")
public class CatalogoRedazione {


    @Id
    private String isbn;

    @OneToOne
    @MapsId
    @JoinColumn(name = "isbn", referencedColumnName = "isbn")
    private Libro libro;

    @Column(nullable = false)
    private Genere genere;

    @Column(columnDefinition = "TEXT")
    private String recensione;

    private Double valutazioneRedazione;

    @Column(nullable = false)
    private LocalDateTime dataInserimento = LocalDateTime.now();

    @Column(nullable = false)
    private boolean visibile = true;

    public CatalogoRedazione() {}



    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public Genere getGenere() { return genere; }
    public void setGenere(Genere genere) { this.genere = this.genere; }

    public String getRecensione() { return recensione; }
    public void setRecensione(String recensione) { this.recensione = recensione; }

    public Double getValutazioneRedazione() { return valutazioneRedazione; }
    public void setValutazioneRedazione(Double valutazioneRedazione) { this.valutazioneRedazione = valutazioneRedazione; }

    public LocalDateTime getDataInserimento() { return dataInserimento; }
    public void setDataInserimento(LocalDateTime dataInserimento) { this.dataInserimento = dataInserimento; }

    public boolean isVisibile() { return visibile; }
    public void setVisibile(boolean visibile) { this.visibile = visibile; }
}