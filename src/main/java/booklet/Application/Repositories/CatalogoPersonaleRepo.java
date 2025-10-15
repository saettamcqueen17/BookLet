package booklet.Application.Repositories;

import booklet.Application.Entities.CatalogoPersonale;
import booklet.Application.Entities.CatalogoPersonale.Scaffale;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CatalogoPersonaleRepo extends JpaRepository<CatalogoPersonale, Long> {


    /**
     * Restituisce tutti i libri nel catalogo personale di un utente.
     */
    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        SELECT c FROM CatalogoPersonale c
         JOIN FETCH c.libro l
        WHERE c.utente.id = :utenteId
        ORDER BY c.addedAt DESC
    """)
    List<CatalogoPersonale> findByUtenteId(@Param("utenteId") UUID utenteId);


    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        SELECT c FROM CatalogoPersonale c
         JOIN FETCH c.libro l
        WHERE c.utente.id = :utenteId AND c.libro.isbn = :libroisbn
    """)
    Optional<CatalogoPersonale> findByUtenteIdAndLibroId(@Param("utenteId") UUID utenteId,
                                                         @Param("libroId") Long libroId);



    @Override
    @PreAuthorize("@ownership.check(#entity.utente.id)")
    <S extends CatalogoPersonale> S save(@Param("entity") S entity);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        UPDATE CatalogoPersonale c
           SET c.scaffale = :scaffale
         WHERE c.utente.id = :utenteId
           AND c.libro.isbn = :libroisbn
    """)
    int aggiornaScaffale(@Param("utenteId") UUID utenteId,
                         @Param("libroId") Long libroId,
                         @Param("scaffale") Scaffale scaffale);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @PreAuthorize("@ownership.check(#utenteId)")
    @Query("""
        UPDATE CatalogoPersonale c
           SET c.rating = :rating,
               c.recensione = :recensione
         WHERE c.utente.id = :utenteId
           AND c.libro.isbn = :libroisbn
    """)
    int aggiornaValutazioneERecensione(@Param("utenteId") UUID utenteId,
                                       @Param("libroId") Long libroId,
                                       @Param("rating") Integer rating,
                                       @Param("recensione") String recensione);



    @PreAuthorize("@ownership.check(#utenteId)")
    void deleteByUtenteIdAndLibroId(@Param("utenteId") UUID utenteId,
                                    @Param("libroId") Long libroId);

    @PreAuthorize("@ownership.check(#utenteId)")
    void deleteAllByUtenteId(@Param("utenteId") UUID utenteId);



    @PreAuthorize("@ownership.check(#utenteId)")
    boolean existsByUtenteIdAndLibroId(@Param("utenteId") UUID utenteId,
                                       @Param("libroId") Long libroId);
}