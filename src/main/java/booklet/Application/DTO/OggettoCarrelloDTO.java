package booklet.Application.DTO;

import java.math.BigDecimal;


public class OggettoCarrelloDTO {

    private String id;
    private String nome;
    private BigDecimal prezzoUnitario;
    private Integer quantita;

    public OggettoCarrelloDTO() {
    }

    public OggettoCarrelloDTO(String id, String nome, BigDecimal prezzoUnitario, Integer quantita) {
        this.id = id;
        this.nome = nome;
        this.prezzoUnitario = prezzoUnitario;
        this.quantita = quantita;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
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
