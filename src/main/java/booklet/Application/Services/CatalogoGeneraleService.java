package booklet.Application.Services;

import booklet.Application.DTO.LibroDTO;
import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Entities.Libro;
import booklet.Application.Mappers.LibroMapper; // puoi riutilizzarlo se mappa i campi uguali
import booklet.Application.Repositories.CatalogoGeneraleRepo;

import booklet.Application.Repositories.LibroRepo;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Primary
@Service
public class CatalogoGeneraleService implements CarrelloService.CatalogQueryPort {

    private final LibroRepo libroRepo;
    private final CatalogoGeneraleRepo repo;

    public CatalogoGeneraleService(LibroRepo libroRepo, CatalogoGeneraleRepo repo) {
        this.libroRepo = libroRepo;
        this.repo = repo;
    }


    @Override
    public Optional<CarrelloService.BookSnapshot> findByIsbn(String rawIsbn) {
        final String isbn = normalizzaIsbn(rawIsbn);
        System.out.println("🔎 [CATALOGO] Ricevuto rawIsbn='" + rawIsbn + "' → normalizzato='" + isbn + "'");
        Optional<CatalogoGenerale> result = repo.findByIsbn(isbn);
        System.out.println("📗 [CATALOGO] Repo.findByIsbn ha trovato qualcosa? " + result.isPresent());
        return result.map(c -> new CarrelloService.BookSnapshot(
                c.getIsbn(),
                c.getTitolo(),
                c.getPrezzo(),
                c.getDisponibilita()
        ));
    }

    public Page<LibroDTO> findAll(Pageable pageable) {
        return repo.findAll(pageable).map(LibroMapper::toDto);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LibroDTO aggiungiLibro(LibroDTO dto) {
        Objects.requireNonNull(dto, "dto nullo");
        if (dto.getIsbn() == null || dto.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN mancante");
        }
        String isbn = normalizzaIsbn(dto.getIsbn());
        if (repo.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("ISBN già presente nel catalogo: " + isbn);
        }

        CatalogoGenerale entity = LibroMapper.toCatalogoGenerale(dto); // mapper adattato
        entity.setIsbn(isbn);
        validaNonNegativo(entity.getDisponibilita(), "disponibilità");
        validaPrezzo(entity.getPrezzo());

        // Salva nel catalogo generale
        CatalogoGenerale saved = repo.save(entity);

        // 🔥 Sincronizza nella tabella Libro (senza prezzo e disponibilità per il catalogo personale/redazione)
        if (!libroRepo.existsByIsbn(isbn)) {
            Libro libro = new Libro();
            libro.setIsbn(isbn);
            libro.setTitolo(entity.getTitolo());
            libro.setAutore(entity.getAutore());
            libro.setCasaEditrice(entity.getCasa_editrice());
            libro.setGenere(entity.getGenere());
            libro.setImmagineLibro(entity.getImmagineLibro());
            // NON impostiamo prezzo e disponibilità - questi restano nel CatalogoGenerale
            libro.setPrezzo(BigDecimal.ZERO); // valore di default
            libro.setDisponibilita(0); // valore di default
            libroRepo.save(libro);
        }

        return LibroMapper.toDto(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public LibroDTO modificaLibro(LibroDTO dto) {
        Objects.requireNonNull(dto, "dto nullo");
        if (dto.getIsbn() == null || dto.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN mancante");
        }
        String isbn = normalizzaIsbn(dto.getIsbn());

        // Verifica che il libro esista
        CatalogoGenerale entity = repo.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ISBN: " + isbn));

        // Aggiorna i campi
        entity.setTitolo(dto.getTitolo());
        entity.setAutore(dto.getAutore());
        entity.setCasa_editrice(dto.getCasaEditrice());
        entity.setGenere(dto.getGenere());
        entity.setImmagineLibro(dto.getImmagineLibro());
        entity.setPrezzo(dto.getPrezzo());
        entity.setDisponibilita(dto.getDisponibilita());

        validaNonNegativo(entity.getDisponibilita(), "disponibilità");
        validaPrezzo(entity.getPrezzo());

        // Salva le modifiche
        CatalogoGenerale updated = repo.save(entity);

        // 🔥 Sincronizza anche nella tabella Libro
        Optional<Libro> libroOpt = libroRepo.findByIsbn(isbn);
        if (libroOpt.isPresent()) {
            Libro libro = libroOpt.get();
            libro.setTitolo(entity.getTitolo());
            libro.setAutore(entity.getAutore());
            libro.setCasaEditrice(entity.getCasa_editrice());
            libro.setGenere(entity.getGenere());
            libro.setImmagineLibro(entity.getImmagineLibro());
            libroRepo.save(libro);
        }

        return LibroMapper.toDto(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<LibroDTO> aggiungiLibri(List<LibroDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();

        Map<String, CatalogoGenerale> daSalvare = new LinkedHashMap<>();
        Map<String, Libro> libriDaSalvare = new LinkedHashMap<>();

        for (LibroDTO d : dtos) {
            Objects.requireNonNull(d, "dto nullo nella lista");
            if (d.getIsbn() == null || d.getIsbn().isBlank()) {
                throw new IllegalArgumentException("ISBN mancante in un elemento");
            }
            String isbn = normalizzaIsbn(d.getIsbn());
            if (repo.existsByIsbn(isbn) || daSalvare.containsKey(isbn)) {
                throw new IllegalArgumentException("ISBN duplicato: " + isbn);
            }

            CatalogoGenerale e = LibroMapper.toCatalogoGenerale(d);
            e.setIsbn(isbn);
            validaNonNegativo(e.getDisponibilita(), "disponibilità");
            validaPrezzo(e.getPrezzo());
            daSalvare.put(isbn, e);

            // 🔥 Prepara anche il Libro per la sincronizzazione
            if (!libroRepo.existsByIsbn(isbn)) {
                Libro libro = new Libro();
                libro.setIsbn(isbn);
                libro.setTitolo(e.getTitolo());
                libro.setAutore(e.getAutore());
                libro.setCasaEditrice(e.getCasa_editrice());
                libro.setGenere(e.getGenere());
                libro.setImmagineLibro(e.getImmagineLibro());
                libro.setPrezzo(BigDecimal.ZERO); // valore di default
                libro.setDisponibilita(0); // valore di default
                libriDaSalvare.put(isbn, libro);
            }
        }

        // Salva tutti i CatalogoGenerale
        List<CatalogoGenerale> saved = repo.saveAll(daSalvare.values());

        // 🔥 Salva tutti i Libro
        if (!libriDaSalvare.isEmpty()) {
            libroRepo.saveAll(libriDaSalvare.values());
        }

        return saved.stream()
                .map(LibroMapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void rimuoviLibroPerIsbn(String rawIsbn) {
        String isbn = normalizzaIsbn(rawIsbn);

        // Rimuove dal catalogo generale
        repo.deleteByIsbn(isbn);

        // 🔥 Rimuove anche dalla tabella Libro se esiste
        if (libroRepo.existsByIsbn(isbn)) {
            libroRepo.deleteByIsbn(isbn);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void rimuoviLibriPerIsbn(Collection<String> rawIsbns) {
        if (rawIsbns == null) return;
        for (String raw : rawIsbns) {
            if (raw == null) continue;
            repo.deleteByIsbn(normalizzaIsbn(raw));
        }
    }

    /* ======== Checkout ======== */
    @Transactional
    public void aggiornaDisponibilitaPerCheckout(Map<String, Integer> libriEQuantita) {
        if (libriEQuantita == null || libriEQuantita.isEmpty()) return;

        for (Map.Entry<String, Integer> e : libriEQuantita.entrySet()) {
            final String isbn = normalizzaIsbn(e.getKey());
            final Integer qta = e.getValue();
            if (qta == null || qta <= 0) {
                throw new IllegalArgumentException("Quantità non valida per ISBN " + isbn + ": " + qta);
            }
        }

        for (Map.Entry<String, Integer> e : libriEQuantita.entrySet()) {
            String isbn = normalizzaIsbn(e.getKey());
            int qta = e.getValue();
            int updated = repo.decrementaDisponibilitaSeSufficiente(isbn, qta);
            if (updated == 0) {
                throw new IllegalStateException("Stock insufficiente o libro non trovato per ISBN " + isbn);
            }
        }
    }


    private static String normalizzaIsbn(String raw) {
        return raw == null ? null : raw.replaceAll("[^0-9Xx]", "").toUpperCase();
    }

    private static void validaNonNegativo(Integer value, String campo) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException("Valore negativo per " + campo);
        }
    }

    public List<LibroDTO> getCatalogoDisponibile() {
        return repo.findByDisponibilitaGreaterThan(0)
                .stream()
                .map(LibroMapper::toDto)
                .collect(Collectors.toList());
    }
    private static void validaPrezzo(BigDecimal prezzo) {
        if (prezzo == null || prezzo.signum() < 0) {
            throw new IllegalArgumentException("Prezzo mancante o negativo");
        }
    }

    /**
     * 🔥 Metodo di sincronizzazione per i libri già esistenti nel CatalogoGenerale
     * che non sono ancora presenti nella tabella Libro.
     * Da chiamare una tantum per risolvere l'inconsistenza esistente.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public int sincronizzaLibriEsistenti() {
        List<CatalogoGenerale> tuttiLibri = repo.findAll();
        int sincronizzati = 0;

        for (CatalogoGenerale cg : tuttiLibri) {
            String isbn = cg.getIsbn();

            // Se il libro non esiste nella tabella Libro, lo creiamo
            if (!libroRepo.existsByIsbn(isbn)) {
                Libro libro = new Libro();
                libro.setIsbn(isbn);
                libro.setTitolo(cg.getTitolo());
                libro.setAutore(cg.getAutore());
                libro.setCasaEditrice(cg.getCasa_editrice());
                libro.setGenere(cg.getGenere());
                libro.setImmagineLibro(cg.getImmagineLibro());
                libro.setPrezzo(BigDecimal.ZERO); // valore di default, il prezzo reale è nel CatalogoGenerale
                libro.setDisponibilita(0); // valore di default, la disponibilità reale è nel CatalogoGenerale

                libroRepo.save(libro);
                sincronizzati++;
            }
        }

        return sincronizzati;
    }

}

