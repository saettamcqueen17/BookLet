package DTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarrelloDTO {

    private List<OggettoCarrelloDTO> oggetti = new ArrayList<>();
    private Integer numeroTotaleOggetti;
    private BigDecimal totale;

    public CarrelloDTO() {
    }

    public CarrelloDTO(List<OggettoCarrelloDTO> oggetti,
                       Integer numeroTotaleOggetti,
                       BigDecimal totale) {
        setOggetti(oggetti);
        this.numeroTotaleOggetti = numeroTotaleOggetti;
        this.totale = totale;
    }

    public List<OggettoCarrelloDTO> getOggetti() {
        return Collections.unmodifiableList(oggetti);
    }

    public void setOggetti(List<OggettoCarrelloDTO> oggetti) {
        this.oggetti = oggetti == null ? new ArrayList<>() : new ArrayList<>(oggetti);
    }

    public Integer getNumeroTotaleOggetti() {
        return numeroTotaleOggetti;
    }

    public void setNumeroTotaleOggetti(Integer numeroTotaleOggetti) {
        this.numeroTotaleOggetti = numeroTotaleOggetti;
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }
}
