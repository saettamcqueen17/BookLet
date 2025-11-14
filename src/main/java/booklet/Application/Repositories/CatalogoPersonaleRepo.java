package booklet.Application.Repositories;

import booklet.Application.Entities.CatalogoPersonale;
import booklet.Application.Entities.CatalogoPersonale.Scaffale;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoPersonaleRepo extends JpaRepository<CatalogoPersonale, Long> {

    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        SELECT c
        FROM CatalogoPersonale c
        JOIN FETCH c.libro l
        WHERE c.utente.utenteId = :utenteId
        ORDER BY c.addedAt DESC
    """)
    List<CatalogoPersonale> findByUtenteId(@Param("utenteId") String utenteId);

    // ✅ Trova un singolo record per utente + libro
    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        SELECT c
        FROM CatalogoPersonale c
        JOIN FETCH c.libro l
        WHERE c.utente.utenteId = :utenteId
          AND c.libro.isbn = :libroIsbn
    """)
    Optional<CatalogoPersonale> findByUtenteIdAndLibroIsbn(@Param("utenteId") String utenteId,
                                                           @Param("libroIsbn") String libroIsbn);

    // ✅ Salvataggio con controllo di proprietà
    @Override
    @PreAuthorize("@ownership.check(#entity.utente.utenteId)")
    <S extends CatalogoPersonale> S save(@Param("entity") S entity);

    // ✅ Aggiornamento scaffale
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        UPDATE CatalogoPersonale c
           SET c.scaffale = :scaffale
         WHERE c.utente.utenteId = :utenteId
           AND c.libro.isbn = :libroIsbn
    """)
    int aggiornaScaffale(@Param("utenteId") String utenteId,
                         @Param("libroIsbn") String libroIsbn,
                         @Param("scaffale") Scaffale scaffale);

    // ✅ Aggiornamento rating e recensione
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        UPDATE CatalogoPersonale c
           SET c.rating = :rating,
               c.recensione = :recensione
         WHERE c.utente.utenteId = :utenteId
           AND c.libro.isbn = :libroIsbn
    """)
    int aggiornaValutazioneERecensione(@Param("utenteId") String utenteId,
                                       @Param("libroIsbn") String libroIsbn,
                                       @Param("rating") Integer rating,
                                       @Param("recensione") String recensione);

    @PreAuthorize("@ownership.check(#utenteId)")
    void deleteByUtente_UtenteIdAndLibro_Isbn(@Param("utenteId") String utenteId,
                                              @Param("libroIsbn") String libroIsbn);

    @PreAuthorize("@ownership.check(#utenteId)")
    void deleteAllByUtente_UtenteId(@Param("utenteId") String utenteId);

    @PreAuthorize("@ownership.check(#utenteId)")
    boolean existsByUtente_UtenteIdAndLibro_Isbn(@Param("utenteId") String utenteId,
                                                 @Param("libroIsbn") String libroIsbn);
}
