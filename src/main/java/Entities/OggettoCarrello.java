package Entities;

import java.math.BigDecimal;




import java.util.Objects;


public class OggettoCarrello {

    private final String id;
    private final String nome;
    private final BigDecimal prezzoUnitario;
    private int quantita;


    public OggettoCarrello(String id, String nome, BigDecimal prezzoUnitario, int quantita) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'id del prodotto non può essere nullo o vuoto");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del prodotto non può essere nullo o vuoto");
        }
        if (prezzoUnitario == null || prezzoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Il prezzo unitario deve essere maggiore o uguale a zero");
        }
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere strettamente positiva");
        }
        this.id = id;
        this.nome = nome;
        this.prezzoUnitario = prezzoUnitario;
        this.quantita = quantita;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public int getQuantita() {
        return quantita;
    }


    public void impostaQuantita(int nuovaQuantita) {
        if (nuovaQuantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere strettamente positiva");
        }
        this.quantita = nuovaQuantita;
    }


    public void aggiungiQuantita(int quantitaDaAggiungere) {
        if (quantitaDaAggiungere <= 0) {
            throw new IllegalArgumentException("La quantità da aggiungere deve essere strettamente positiva");
        }
        this.quantita += quantitaDaAggiungere;
    }


    public BigDecimal getTotale() {
        return prezzoUnitario.multiply(BigDecimal.valueOf(quantita));
    }


    public OggettoCarrello copia() {
        return new OggettoCarrello(id, nome, prezzoUnitario, quantita);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OggettoCarrello that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OggettoCarrello{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", prezzoUnitario=" + prezzoUnitario +
                ", quantita=" + quantita +
                '}';
    }
}

