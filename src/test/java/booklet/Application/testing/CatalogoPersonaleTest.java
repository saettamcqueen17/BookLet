package booklet.Application.testing;


import booklet.Application.Entities.CatalogoPersonale;
import booklet.Application.Entities.Genere;
import booklet.Application.Entities.Libro;
import booklet.Application.Entities.Utente;
import booklet.Application.Repositories.CatalogoGeneraleRepo;
import booklet.Application.Repositories.CatalogoPersonaleRepo;
import booklet.Application.Repositories.UtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan(basePackages = "booklet.Application.Entities")
@EnableJpaRepositories(basePackages = "booklet.Application.Repositories")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class CatalogoPersonaleRepoTest {

    @Autowired
    private CatalogoPersonaleRepo cpRepo;

    @Autowired
    private CatalogoGeneraleRepo libriRepo;

    @Autowired
    private UtenteRepository utenteRepo;

    private Utente u;
    private Libro l;

    @BeforeEach
    void seed() {
        // Libro
        l = new Libro();
        l.setIsbn("ISBN-CP-1");
        l.setTitolo("Libro CP");
        l.setPrezzo(new BigDecimal("12.50"));
        l.setDisponibilita(3);
        l.setAutore("Umberto Eco");
        l.setCasaEditrice("Bompiani");
        l.setGenere(Genere.ROMANZO_ROMANTICO);

        libriRepo.saveAndFlush(l);


        u = new Utente();

        try {
            Utente.class.getDeclaredMethod("setUsername", String.class).invoke(u, "user1");
            Utente.class.getDeclaredMethod("setEmail", String.class).invoke(u, "user1@example.com");
            Utente.class.getDeclaredMethod("setPasswordHash", String.class).invoke(u, "x");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Aggiungi setter a Utente: username, email, passwordHash");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        u = utenteRepo.saveAndFlush(u);
    }

    @Test
    void uniqueConstraint_sulPairUtenteLibro() {
        CatalogoPersonale r1 = new CatalogoPersonale();
        r1.setUtente(u);
        r1.setLibro(l);
        cpRepo.saveAndFlush(r1);

        CatalogoPersonale r2 = new CatalogoPersonale();
        r2.setUtente(u);
        r2.setLibro(l);

        assertThrows(DataIntegrityViolationException.class, () -> {
            cpRepo.saveAndFlush(r2);
        }, "La coppia (utente,libro) è unique -> deve fallire");
    }
}