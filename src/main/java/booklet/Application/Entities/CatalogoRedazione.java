package booklet.Application.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_redazione")
public class CatalogoRedazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn")
    private Libro libro;

    @Column(name = "data_inserimento", nullable = true)
    private LocalDateTime dataInserimento;

    @Column(name = "genere")
    private String genere;

    @Column(name = "recensione")
    private String recensione;

    @Column(name = "valutazione_redazione")
    private Double valutazioneRedazione;

    @Column(name = "visibile")
    private Boolean visibile;

    public CatalogoRedazione(Libro libro) {
        this.libro = libro ;
    }

    public CatalogoRedazione() {

    }

    public Libro getLibro() {
        return libro ;
    }

    public String getRecensione() {
        return recensione ;
    }

    public double getValutazioneRedazione() {
        return valutazioneRedazione ;
    }

    public boolean getVisibile() {
        return visibile ;
    }

    public void setVisibile(boolean visibile) {
        this.visibile = visibile ;
    }

    public void setRecensione(String recensione) {
        this.recensione = recensione
                 ;
    }

    public void setValutazioneRedazione(Double valutazione) {
        this.valutazioneRedazione = valutazione ;
    }

    public void setGenere(Genere genere) {
       this.genere = genere.toString();
    }


    public void setDataInserimento(LocalDateTime dataInserimento){
        this.dataInserimento = dataInserimento;
    }


}