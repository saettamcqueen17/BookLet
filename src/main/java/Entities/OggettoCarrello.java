package Entities;

import java.math.BigDecimal;

public class OggettoCarrello {

    private Long bookId;
    private String titolo;
    private BigDecimal unitPrice;
    private int quantity;

    public OggettoCarrello(Long bookId, String titolo, BigDecimal unitPrice, int quantity) {
        this.bookId = bookId;
        this.titolo = titolo;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }


    public Long getBookId() {

        return bookId ;
    }


    public String getTitolo() {
        return titolo;
    }
}
