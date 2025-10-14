package booklet.Application.Entities;



import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class Carrello {

    private final Map<String, OggettoCarrello> libriNelCarrello = new LinkedHashMap<>();


    public void aggiungiOggetto(OggettoCarrello oggetto) {
        Objects.requireNonNull(oggetto, "L'oggetto da aggiungere non può essere nullo");

        libriNelCarrello.compute(oggetto.getISBN(), (isbn, esistente) -> {
            if (esistente == null) {
                return oggetto.copia();
            }
            verificaCompatibilita(esistente, oggetto);
            esistente.aggiungiQuantita(oggetto.getQuantita());
            return esistente;
        });
    }

    private void verificaCompatibilita(OggettoCarrello esistente, OggettoCarrello nuovo) {
        if (!Objects.equals(esistente.getNome(), nuovo.getNome())) {
            throw new IllegalArgumentException("Nome prodotto differente per lo stesso id");
        }
        if (esistente.getPrezzoUnitario().compareTo(nuovo.getPrezzoUnitario()) != 0) {
            throw new IllegalArgumentException("Prezzo differente per lo stesso id");
        }
    }


    public void aggiornaQuantita(String oggettoId, int nuovaQuantita) {
        OggettoCarrello oggetto = libriNelCarrello.get(Objects.requireNonNull(oggettoId, "L'id non può essere nullo"));
        if (oggetto == null) {
            throw new NoSuchElementException("Oggetto con id " + oggettoId + " non presente nel carrello");
        }
        if (nuovaQuantita <= 0) {
            libriNelCarrello.remove(oggettoId);
        } else {
            oggetto.impostaQuantita(nuovaQuantita);
        }
    }


    public void rimuoviOggetto(String oggettoId) {
        libriNelCarrello.remove(Objects.requireNonNull(oggettoId, "L'id non può essere nullo"));
    }


    public void svuota() {
        libriNelCarrello.clear();
    }


    public boolean isVuoto() {
        return libriNelCarrello.isEmpty();
    }


    public int getNumeroTotaleOggetti() {
        return libriNelCarrello.values().stream()
                .mapToInt(OggettoCarrello::getQuantita)
                .sum();
    }


    public List<OggettoCarrello> getLibriNelCarrello() {
        return libriNelCarrello.values().stream()
                .map(OggettoCarrello::copia)
                .collect(Collectors.toUnmodifiableList());
    }


    public Optional<OggettoCarrello> getOggetto(String oggettoId) {
        Objects.requireNonNull(oggettoId, "L'id non può essere nullo");
        OggettoCarrello oggetto = libriNelCarrello.get(oggettoId);
        return Optional.ofNullable(oggetto == null ? null : oggetto.copia());
    }


    public BigDecimal calcolaTotale() {
        return libriNelCarrello.values().stream()
                .map(OggettoCarrello::getTotale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

