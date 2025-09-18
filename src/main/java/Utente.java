import java.util.UUID;


@Entity
@Table(name = "utente")
public class Utente {


    @Id
    @GeneratedValue
    private UUID id;                // Hibernate 6 gestisce UUID
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;


}
