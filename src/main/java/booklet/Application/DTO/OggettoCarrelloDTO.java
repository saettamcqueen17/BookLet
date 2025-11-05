package booklet.Application.DTO;

import java.math.BigDecimal;


public class OggettoCarrelloDTO {

    private String isbn;
    private String titolo ;
    private BigDecimal prezzoUnitario;
    private Integer quantita;



    public OggettoCarrelloDTO(String isbn,String titolo , BigDecimal prezzoUnitario, Integer quantita) {
        this.isbn = isbn;
        this.titolo = titolo ;
        this.prezzoUnitario = prezzoUnitario;
        this.quantita = quantita;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setId(String isbn) {
        this.isbn = isbn;
    }



    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setPrezzoUnitario(BigDecimal prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }


}
