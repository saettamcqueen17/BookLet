package booklet.Application.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "catalogo_generale",
        uniqueConstraints = @UniqueConstraint(columnNames = "isbn"),
        indexes = {
                @Index(name = "ix_libri_titolo", columnList = "titolo"),
                @Index(name = "ix_libri_autore", columnList = "autore"),
                @Index(name = "ix_libri_genere", columnList = "genere")
        }
)
public class CatalogoGenerale {



   @Id @Column(nullable = false, length = 20)
    private String isbn; // normalizza lato service (niente spazi/trattini)

    @Column(nullable = false, length = 255)
    private String titolo;

    @Column(length = 255)
    private String autore;

    @Enumerated(EnumType.STRING)
    @Column(length = 80)
    private Genere genere;

    @Column(name = "anno_pubblicazione")
    private Integer anno_pubblicazione ;

    @Column(name = "casa_editrice")
    private String casa_editrice;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal prezzo = BigDecimal.ZERO;

    @Column(name = "immagine_copertina")
    private String immagineLibro;



    /** null = nessun limite hard di stock */
    @Column(name = "disponibilita")
    private Integer disponibilita;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate void touch(){ this.updatedAt = Instant.now(); }


    public String getIsbn() { return isbn; }

 public String getCasa_editrice() {
  return casa_editrice;
 }

 public void setCasa_editrice(String casa_editrice) {
  this.casa_editrice = casa_editrice;
 }

 public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }
    public Genere getGenere() { return genere; }


    public void setGenere(Genere genere) { this.genere = genere; }
 public String getImmagineLibro(){
  return immagineLibro ;
 }

 public void setImmagineLibro(String immagineLibro) {
  this.immagineLibro = immagineLibro;
 }

 public Integer getAnnoPubblicazione() { return anno_pubblicazione; }
    public void setAnnoPubblicazione(Integer annoPubblicazione) { this.anno_pubblicazione = annoPubblicazione; }
    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }
    public Integer getDisponibilita() { return disponibilita; }
    public void setDisponibilita(Integer disponibilita) { this.disponibilita = disponibilita; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

 private Libro convertToLibro(CatalogoGenerale cg) {
  Libro libro = new Libro();
  libro.setIsbn(cg.getIsbn());
  libro.setTitolo(cg.getTitolo());
  libro.setAutore(cg.getAutore());
  libro.setCasaEditrice(cg.getCasa_editrice());
  libro.setPrezzo(cg.getPrezzo());
  libro.setGenere(cg.getGenere());
  libro.setImmagineLibro(cg.getImmagineLibro());
  libro.setDisponibilita(cg.getDisponibilita());
  return libro;
 }

}