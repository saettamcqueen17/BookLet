package booklet.Application.Repositories;

import booklet.Application.Entities.Libro;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CatalogoGeneraleRepo extends JpaRepository<Libro, Long> {

    Optional<Libro> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           UPDATE Libro l
              SET l.disponibilita = l.disponibilita - :qta
            WHERE l.isbn = :isbn
              AND (l.disponibilita IS NULL OR l.disponibilita >= :qta)
           """)
    int decrementaDisponibilitaSeSufficiente(@Param("isbn") String isbn, @Param("qta") int quantita);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Libro l SET l.disponibilita = COALESCE(l.disponibilita,0) + :qta WHERE l.isbn = :isbn")
    int incrementaDisponibilita(@Param("isbn") String isbn, @Param("qta") int quantita);

    @Transactional
    void deleteByIsbn(String isbn);
}