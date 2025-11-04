//package booklet.Application.testing;
//
//import booklet.Application.DTO.CarrelloDTO;
//import booklet.Application.Services.CarrelloService;
//import booklet.Application.Services.CarrelloService.BookSnapshot;
//import booklet.Application.Services.CarrelloService.CatalogQueryPort;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class CarrelloServiceTest {
//
//    private static class FakeCatalog implements CatalogQueryPort {
//        private final Map<String, BookSnapshot> db = new HashMap<>();
//        void put(String isbn, String titolo, BigDecimal prezzo, Integer stock) {
//            db.put(isbn, new BookSnapshot(isbn, titolo, prezzo, stock));
//        }
//        @Override public Optional<BookSnapshot> findByIsbn(String isbn) {
//            return Optional.ofNullable(db.get(isbn));
//        }
//    }
//
//    private FakeCatalog catalog;
//    private CarrelloService service;
//    private String userId;
//
//    @BeforeEach
//    void setUp() {
//        catalog = new FakeCatalog();
//        // libri di test
//        catalog.put("123", "Il nome della rosa", new BigDecimal("20.00"), 10);
//        catalog.put("456", "Il Gattopardo", new BigDecimal("15.50"), null); // stock illimitato (null)
//
//        service = new CarrelloService(catalog);
//        userId = "giuseppino";
//    }
//
//    @Test
//    void aggiungiLibro_success() {
//        CarrelloDTO cart = service.aggiungiLibro(userId, "123", 2);
//
//        assertEquals(1, cart.getOggetti().size());
//        assertEquals(2, cart.getNumeroTotaleOggetti());
//        assertEquals(0, cart.getTotale().compareTo(new BigDecimal("40.00")));
//        assertEquals("123", cart.getOggetti().get(0).getIsbn());
//        assertEquals(2, cart.getOggetti().get(0).getQuantita());
//    }
//
//    @Test
//    void aggiungiLibro_rifiutaSeNonInCatalogo() {
//        assertThrows(IllegalArgumentException.class,
//                () -> service.aggiungiLibro(userId, "999", 1));
//    }
//
//    @Test
//    void aggiungiLibro_rifiutaSeStockZero() {
//        catalog.put("777", "Esaurito", new BigDecimal("10.00"), 0);
//        assertThrows(IllegalArgumentException.class,
//                () -> service.aggiungiLibro(userId, "777", 1));
//    }
//
//    @Test
//    void aggiornaQuantita_impostaNuovaQuantita() {
//        service.aggiungiLibro(userId, "123", 2);
//        CarrelloDTO cart = service.aggiornaQuantita(userId, "123", 5);
//
//        assertEquals(1, cart.getOggetti().size());
//        assertEquals(5, cart.getNumeroTotaleOggetti());
//        assertEquals(0, cart.getTotale().compareTo(new BigDecimal("100.00")));
//    }
//
//    @Test
//    void aggiornaQuantita_creaRigaSeMancante_maVerificaStock() {
//        // non c'è in carrello; nuovaQuantita=3 -> crea riga
//        CarrelloDTO cart = service.aggiornaQuantita(userId, "456", 3);
//        assertEquals(1, cart.getOggetti().size());
//        assertEquals(3, cart.getNumeroTotaleOggetti());
//        assertEquals("456", cart.getOggetti().get(0).getIsbn());
//        assertEquals(0, cart.getTotale().compareTo(new BigDecimal("46.50")));
//    }
//
//    @Test
//    void aggiornaQuantita_rifiutaSeEccedeStock() {
//        service.aggiungiLibro(userId, "123", 2);
//        assertThrows(IllegalArgumentException.class,
//                () -> service.aggiornaQuantita(userId, "123", 11)); // stock 10
//    }
//
//    @Test
//    void rimuoviLibro_e_svuota_funzionano() {
//        service.aggiungiLibro(userId, "123", 2);
//        service.aggiungiLibro(userId, "456", 1);
//
//        CarrelloDTO afterRemove = service.rimuoviLibro(userId, "456");
//        assertEquals(1, afterRemove.getOggetti().size());
//
//        CarrelloDTO afterClear = service.svuota(userId);
//        assertTrue(afterClear.getOggetti().isEmpty());
//        assertEquals(0, afterClear.getNumeroTotaleOggetti());
//        assertEquals(0, afterClear.getTotale().compareTo(new BigDecimal("0.00")));
//    }
//
//    @Test
//    void getCarrello_nonNulloEConsistente() {
//        CarrelloDTO empty = service.getCarrello(userId);
//        assertNotNull(empty);
//        assertTrue(empty.getOggetti().isEmpty());
//    }
