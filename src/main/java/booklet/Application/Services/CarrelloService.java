package booklet.Application.Services;

import booklet.Application.DTO.CarrelloDTO;
import booklet.Application.Entities.*;
import booklet.Application.Mappers.CarrelloMapper;
import booklet.Application.Repositories.CatalogoPersonaleRepo;
import booklet.Application.Repositories.LibroRepo;
import booklet.Application.Repositories.UtenteRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service in-memory per la gestione del carrello.
 * - Accetta  isbn + quantita dal client.
 * prezzo titolo e disponibilità dal DB.

 */


@Service
@Transactional
public class CarrelloService {

    private final Map<String, Carrello> carrelliPerUtente = new ConcurrentHashMap<>();
    private  CatalogoGeneraleService catalog; // porta server-side per leggere i dati autorevoli del libro

    private UtenteRepository utenteRepository ;
    private LibroRepo libroRepository ;
    private CatalogoPersonaleRepo catalogoPersonaleRepo ;
    public CarrelloService(CatalogoGeneraleService catalog,
                           CatalogoPersonaleRepo catalogoPersonaleRepo,
                           UtenteRepository utenteRepository,
                           LibroRepo libroRepository) {
        this.catalog = catalog;
        this.catalogoPersonaleRepo = catalogoPersonaleRepo;
        this.utenteRepository = utenteRepository;
        this.libroRepository = libroRepository;
    }


    @PreAuthorize("@ownership.check(#utenteId)") // opzionale: rimuovi se non stai ancora usando Security
    public CarrelloDTO aggiungiLibro(String utenteId, String rawIsbn, int quantita) {
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
                carrello.aggiungiOggetto(new OggettoCarrello(isbn,book.titolo , book.prezzo(), quantita));
            } else {
                carrello.aggiornaQuantita(isbn, (int) richiestaTotale);
            }
            return CarrelloMapper.toDto(carrello);
        }
    }

    @PreAuthorize("@ownership.check(#userId)")
    @Transactional
    public void checkout(String userId) {

        Objects.requireNonNull(userId, "L'id utente non può essere nullo");

        Carrello carrello = carrelliPerUtente.get(userId);
        if (carrello == null || carrello.getLibriNelCarrello().isEmpty()) {
            throw new IllegalStateException("Il carrello è vuoto o non esiste per l'utente " + userId);
        }

        Utente utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Utente non trovato nel DB: " + userId));

        ConcurrentHashMap<String, Integer> libriEQuantita = new ConcurrentHashMap<>();
        for (OggettoCarrello oggetto : carrello.getLibriNelCarrello()) {
            libriEQuantita.put(oggetto.getIsbn(), oggetto.getQuantita());
        }

        catalog.aggiornaDisponibilitaPerCheckout(libriEQuantita);

        for (OggettoCarrello oggetto : carrello.getLibriNelCarrello()) {

            String isbn = oggetto.getIsbn();

            Libro libro = libroRepository.findById(isbn)
                    .orElseThrow(() -> new IllegalStateException("Libro non trovato nel DB: " + isbn));

            boolean esisteGia = catalogoPersonaleRepo
                    .existsByUtente_UtenteIdAndLibro_Isbn(userId, isbn);

            if (!esisteGia) {
                CatalogoPersonale voce = new CatalogoPersonale();
                voce.setUtente(utente);
                voce.setLibro(libro);
                voce.setScaffale(CatalogoPersonale.Scaffale.DaLeggere);
                voce.setAddedAt(Instant.now());
                catalogoPersonaleRepo.save(voce);
            }
        }

        carrello.getLibriNelCarrello().clear();
    }


    @PreAuthorize("@ownership.check(#utenteId)")
    public CarrelloDTO aggiornaQuantita(String utenteId, String rawIsbn, int nuovaQuantita) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");
        Objects.requireNonNull(rawIsbn, "L'ISBN non può essere nullo");

        final String isbn = normalizzaIsbn(rawIsbn);
        Carrello carrello = carrelliPerUtente.computeIfAbsent(utenteId, __ -> new Carrello());

        synchronized (carrello) {
            if (nuovaQuantita <= 0) {
                carrello.rimuoviOggetto(isbn);
                return CarrelloMapper.toDto(carrello);
            }

            BookSnapshot book = catalog.findByIsbn(isbn)
                    .orElseThrow(() -> new IllegalArgumentException("Libro non presente nel catalogo generale"));
            if (book.stock() != null && nuovaQuantita > book.stock()) {
                throw new IllegalArgumentException("Quantità eccede disponibilità");
            }


            if (carrello.getOggetto(isbn).isEmpty()) {

                carrello.aggiungiOggetto(new OggettoCarrello(isbn, book.titolo(), book.prezzo(), nuovaQuantita));
            } else {
                carrello.aggiornaQuantita(isbn, nuovaQuantita);
            }
            return CarrelloMapper.toDto(carrello);
        }
    }


    @PreAuthorize("@ownership.check(#utenteId)")
    public CarrelloDTO rimuoviLibro(String utenteId, String rawIsbn) {
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
    public CarrelloDTO svuota(String utenteId) {
        Objects.requireNonNull(utenteId, "L'id utente non può essere nullo");

        Carrello carrello = carrelliPerUtente.remove(utenteId);

        if (carrello == null) {
            return new CarrelloDTO(
                    Collections.emptyList(),
                    0,
                    BigDecimal.ZERO
            );
        }

        synchronized (carrello) {
            carrello.svuota();
            return new CarrelloDTO(
                    Collections.emptyList(),
                    0,
                    BigDecimal.ZERO
            );
        }
    }


 @PreAuthorize("@ownership.check(#utenteId)") public CarrelloDTO getCarrello(String utenteId) {
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