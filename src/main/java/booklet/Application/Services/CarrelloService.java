package booklet.Application.Services;

import booklet.Application.DTO.CarrelloDTO;
import booklet.Application.Entities.Carrello;
import booklet.Application.Entities.OggettoCarrello;
import booklet.Application.Mappers.CarrelloMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service in-memory per la gestione del carrello.
 * - Accetta  isbn + quantita dal client.
 * prezzo titolo e disponibilità dal DB.

 */


@Service
public class CarrelloService {

    private final Map<UUID, Carrello> carrelliPerUtente = new ConcurrentHashMap<>();
    private final CatalogQueryPort catalog; // porta server-side per leggere i dati autorevoli del libro

    public CarrelloService(CatalogQueryPort catalog) {
        this.catalog = catalog;
    }


    @PreAuthorize("@ownership.check(#utenteId)") // opzionale: rimuovi se non stai ancora usando Security
    public CarrelloDTO aggiungiLibro(UUID utenteId, String rawIsbn, int quantita) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");
        Objects.requireNonNull(rawIsbn, "L'ISBN non può essere nullo");
        if (quantita <= 0) throw new IllegalArgumentException("La quantità deve essere maggiore di zero");

        final String isbn = normalizzaIsbn(rawIsbn);


        BookSnapshot book = catalog.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Libro non presente nel catalogo generale"));

        if (book.stock() != null && book.stock() <= 0) {
            throw new IllegalArgumentException("Libro non disponibile");
        }


        Carrello carrello = carrelliPerUtente.computeIfAbsent(utenteId, __ -> new Carrello());


        synchronized (carrello) {
            int qtyAttuale = carrello.getOggetto(isbn).map(OggettoCarrello::getQuantita).orElse(0);
            long richiestaTotale = (long) qtyAttuale + quantita;

            if (book.stock() != null && richiestaTotale > book.stock()) {
                throw new IllegalArgumentException("La quantità richiesta eccede la disponibilità");
            }

            if (qtyAttuale == 0) {
                carrello.aggiungiOggetto(new OggettoCarrello(isbn,  book.prezzo(), quantita));
            } else {
                carrello.aggiornaQuantita(isbn, (int) richiestaTotale);
            }
            return CarrelloMapper.toDto(carrello);
        }
    }


    @PreAuthorize("@ownership.check(#utenteId)")
    public CarrelloDTO aggiornaQuantita(UUID utenteId, String rawIsbn, int nuovaQuantita) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");
        Objects.requireNonNull(rawIsbn, "L'ISBN non può essere nullo");

        final String isbn = normalizzaIsbn(rawIsbn);
        Carrello carrello = carrelliPerUtente.computeIfAbsent(utenteId, __ -> new Carrello());

        synchronized (carrello) {
            if (nuovaQuantita <= 0) {
                carrello.rimuoviOggetto(isbn);
                return CarrelloMapper.toDto(carrello);
            }

            // Verifica su dati server-side
            BookSnapshot book = catalog.findByIsbn(isbn)
                    .orElseThrow(() -> new IllegalArgumentException("Libro non presente nel catalogo generale"));
            if (book.stock() != null && nuovaQuantita > book.stock()) {
                throw new IllegalArgumentException("Quantità eccede disponibilità");
            }


            if (carrello.getOggetto(isbn).isEmpty()) {

                carrello.aggiungiOggetto(new OggettoCarrello(isbn,  book.prezzo(), nuovaQuantita));
            } else {
                carrello.aggiornaQuantita(isbn, nuovaQuantita);
            }
            return CarrelloMapper.toDto(carrello);
        }
    }


    @PreAuthorize("@ownership.check(#utenteId)")
    public CarrelloDTO rimuoviLibro(UUID utenteId, String rawIsbn) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");
        Objects.requireNonNull(rawIsbn, "L'ISBN non può essere nullo");

        final String isbn = normalizzaIsbn(rawIsbn);
        Carrello carrello = carrelliPerUtente.computeIfAbsent(utenteId, __ -> new Carrello());
        synchronized (carrello) {
            carrello.rimuoviOggetto(isbn);
            return CarrelloMapper.toDto(carrello);
        }
    }


    @PreAuthorize("@ownership.check(#utenteId)")
    public CarrelloDTO svuota(UUID utenteId) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");
        Carrello carrello = carrelliPerUtente.computeIfAbsent(utenteId, __ -> new Carrello());
        synchronized (carrello) {
            carrello.svuota();
            return CarrelloMapper.toDto(carrello);
        }
    }




 @PreAuthorize("@ownership.check(#utenteId)") public CarrelloDTO getCarrello(UUID utenteId) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");
        Carrello carrello = carrelliPerUtente.computeIfAbsent(utenteId, __ -> new Carrello());
        synchronized (carrello) {
            return CarrelloMapper.toDto(carrello);
        }
    }



    private String normalizzaIsbn(String isbn) {
        return isbn.replaceAll("[-\\s]", "").toUpperCase();
    }


    public interface CatalogQueryPort {
        Optional<BookSnapshot> findByIsbn(String isbn);
    }


    public record BookSnapshot(
            String isbn,
            String titolo,
            BigDecimal prezzo,
            Integer stock // null => nessun limite hard
    ) {}
}