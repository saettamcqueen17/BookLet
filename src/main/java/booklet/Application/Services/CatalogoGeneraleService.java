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

        return LibroMapper.toDto(repo.save(entity));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public List<LibroDTO> aggiungiLibri(List<LibroDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();

        Map<String, CatalogoGenerale> daSalvare = new LinkedHashMap<>();
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
        }

        return repo.saveAll(daSalvare.values())
                .stream()
                .map(LibroMapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void rimuoviLibroPerIsbn(String rawIsbn) {
        String isbn = normalizzaIsbn(rawIsbn);
        repo.deleteByIsbn(isbn);
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

    public List<Libro> getCatalogoDisponibile() {
        return libroRepo.findByDisponibilitaGreaterThan(0);
    }
    private static void validaPrezzo(BigDecimal prezzo) {
        if (prezzo == null || prezzo.signum() < 0) {
            throw new IllegalArgumentException("Prezzo mancante o negativo");
        }
    }


}