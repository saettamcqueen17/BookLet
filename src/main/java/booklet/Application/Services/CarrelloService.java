package booklet.Application.Services;

import booklet.Application.DTO.CarrelloDTO;
import booklet.Application.Entities.*;
import booklet.Application.Mappers.CarrelloMapper;
import booklet.Application.Repositories.CatalogoPersonaleRepo;
import booklet.Application.Repositories.LibroRepo;
import booklet.Application.Repositories.UtenteRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;




@Service
@Transactional
public class CarrelloService {

    private final Map<String, Carrello> carrelliPerUtente = new ConcurrentHashMap<>();
    private final CatalogoGeneraleService catalog;

    private final UtenteRepository utenteRepository;
    private final LibroRepo libroRepository;
    private final CatalogoPersonaleRepo catalogoPersonaleRepo;
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

        System.out.println("=== AGGIUNGI LIBRO AL CARRELLO ===");
        System.out.println("ISBN: " + isbn);
        System.out.println("Titolo: " + book.titolo());
        System.out.println("Prezzo dal catalogo: " + book.prezzo());
        System.out.println("Quantità richiesta: " + quantita);
        System.out.println("===================================");

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

            // Rimuovo il vecchio oggetto (se esiste) e aggiungo quello nuovo con prezzo aggiornato
            // Questo garantisce che il prezzo sia sempre aggiornato dal catalogo
            carrello.rimuoviOggetto(isbn);
            carrello.aggiungiOggetto(new OggettoCarrello(isbn, book.titolo(), book.prezzo(), (int) richiestaTotale));

            return CarrelloMapper.toDto(carrello);
        }
    }

    @PreAuthorize("@ownership.check(#userId)")
    @Transactional
    public void checkout(String userId, @Valid CarrelloDTO carrelloClient) {

        Objects.requireNonNull(userId, "L'id utente non può essere nullo");
        Objects.requireNonNull(carrelloClient, "Il DTO del carrello non può essere nullo");

        Carrello carrello = carrelliPerUtente.get(userId);
        if (carrello == null || carrello.getLibriNelCarrello().isEmpty()) {
            throw new IllegalStateException("Il carrello è vuoto o non esiste per l'utente " + userId);
        }

        System.out.println("=== CHECKOUT DEBUG ===");
        System.out.println("Totale client inviato: " + carrelloClient.getTotale());
        System.out.println("Numero oggetti nel carrello server: " + carrello.getLibriNelCarrello().size());

        BigDecimal totaleReale = BigDecimal.ZERO;
        List<String> libriConPrezzoModificato = new ArrayList<>();

        for (OggettoCarrello oggetto : carrello.getLibriNelCarrello()) {

            String isbn = oggetto.getIsbn();
            int quantita = oggetto.getQuantita();
            BigDecimal prezzoCarrello = oggetto.getPrezzoUnitario();

            // IMPORTANTE: Recupero il prezzo dalla STESSA FONTE usata quando si aggiunge al carrello
            // Uso CatalogoGeneraleService (tabella catalogo_generale) NON libroRepository (tabella libro)
            BookSnapshot book = catalog.findByIsbn(isbn)
                    .orElseThrow(() -> new IllegalStateException("Libro non trovato nel catalogo: " + isbn));

            BigDecimal prezzoDb = book.prezzo();

            System.out.println(String.format("  Libro: %s", oggetto.getTitolo()));
            System.out.println(String.format("    Prezzo CARRELLO: %s", prezzoCarrello));
            System.out.println(String.format("    Prezzo CATALOGO: %s", prezzoDb));
            System.out.println(String.format("    Uguali? %s", prezzoCarrello.compareTo(prezzoDb) == 0));

            // Verifico che il prezzo nel carrello corrisponda al prezzo attuale nel DB
            if (prezzoCarrello.compareTo(prezzoDb) != 0) {
                libriConPrezzoModificato.add(
                    String.format("ISBN: %s, Titolo: '%s', Prezzo carrello: %.2f, Prezzo attuale: %.2f",
                        isbn, oggetto.getTitolo(), prezzoCarrello, prezzoDb)
                );
            }

            BigDecimal subTot = prezzoDb.multiply(BigDecimal.valueOf(quantita));
            totaleReale = totaleReale.add(subTot);
        }

        System.out.println("Totale REALE calcolato: " + totaleReale);
        System.out.println("Libri con prezzo modificato: " + libriConPrezzoModificato.size());
        System.out.println("======================");

        // Se ci sono libri con prezzi modificati, lancio un'eccezione con i dettagli
        if (!libriConPrezzoModificato.isEmpty()) {
            String dettagli = String.join("; ", libriConPrezzoModificato);
            throw new IllegalStateException(
                    "Alcuni prezzi nel carrello non sono aggiornati. Aggiorna il carrello prima di procedere. " +
                    "Libri con prezzi modificati: " + dettagli
            );
        }

        // Confronto totale inviato dal client e totale reale
        BigDecimal totaleClient = carrelloClient.getTotale();
        if (totaleClient == null || totaleReale.compareTo(totaleClient) != 0) {
            throw new IllegalStateException(
                    "Totale NON valido: " +
                            "totale inviato=" + totaleClient + ", totale reale=" + totaleReale
            );
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

            // Recupero sempre i dati aggiornati dal catalogo (incluso il prezzo)
            BookSnapshot book = catalog.findByIsbn(isbn)
                    .orElseThrow(() -> new IllegalArgumentException("Libro non presente nel catalogo generale"));
            if (book.stock() != null && nuovaQuantita > book.stock()) {
                throw new IllegalArgumentException("Quantità eccede disponibilità");
            }

            // Rimuovo il vecchio oggetto (se esiste) e aggiungo quello nuovo con prezzo aggiornato
            carrello.rimuoviOggetto(isbn);
            carrello.aggiungiOggetto(new OggettoCarrello(isbn, book.titolo(), book.prezzo(), nuovaQuantita));

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