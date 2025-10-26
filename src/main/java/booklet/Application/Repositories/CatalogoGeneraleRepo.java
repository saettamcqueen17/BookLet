package booklet.Application.Repositories;

import booklet.Application.Entities.CatalogoGenerale;
import booklet.Application.Entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;




@Repository
public interface CatalogoGeneraleRepo extends JpaRepository<CatalogoGenerale, String> {

    Optional<CatalogoGenerale> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
         UPDATE CatalogoGenerale c
            SET c.disponibilita = c.disponibilita - :qta
          WHERE c.isbn = :isbn
            AND (c.disponibilita IS NULL OR c.disponibilita >= :qta)
         """)
    int decrementaDisponibilitaSeSufficiente(@Param("isbn") String isbn, @Param("qta") int qta);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE CatalogoGenerale c SET c.disponibilita = COALESCE(c.disponibilita,0) + :qta WHERE c.isbn = :isbn")
    int incrementaDisponibilita(@Param("isbn") String isbn, @Param("qta") int qta);

    void deleteByIsbn(String isbn);

}
