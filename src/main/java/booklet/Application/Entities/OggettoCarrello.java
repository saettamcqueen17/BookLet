package booklet.Application.Entities;

import java.math.BigDecimal;




import java.util.Objects;


public class OggettoCarrello {

    private final String isbn;
    private final String titolo ;
    private final BigDecimal prezzoUnitario;
    private int quantita;


    public OggettoCarrello(String isbn, String titolo, BigDecimal prezzoUnitario, int quantita) {

        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("L'isbn del prodotto non può essere nullo o vuoto");
        }

        if (prezzoUnitario == null || prezzoUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Il prezzo unitario deve essere maggiore o uguale a zero");
        }
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere strettamente positiva");
        }
        this.isbn = isbn;
        this.prezzoUnitario = prezzoUnitario;
        this.quantita = quantita;
        this.titolo = titolo ;
    }

    public String getIsbn() {
        return isbn;
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
        return new OggettoCarrello(isbn,titolo , prezzoUnitario, quantita);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OggettoCarrello that)) {
            return false;
        }
        return Objects.equals(isbn, that.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "OggettoCarrello{" +
                "isbn='" + isbn + '\'' +

                ", prezzoUnitario=" + prezzoUnitario +
                ", quantita=" + quantita +
                '}';
    }


    public String getTitolo() {
        return titolo ;
    }
}

