package booklet.Application.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "utente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utente {

    @Id
    @Column(name = "utente_id", nullable = false, unique = true)
    private String utenteId; // stesso valore del "sub" di Keycloak

    @Column(nullable = false, unique = true)
    private String username;

    private String email;

    private String ruolo; // opzionale, es. "USER" o "ADMIN"

    @CreationTimestamp
    private Instant createdAt;


    public String getUtenteId(){
        return this.utenteId;
    }

    public void setUtenteId(String id_utente) {
        this.utenteId = id_utente;
    }
}