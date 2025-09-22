package Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


import java.util.UUID;


@Entity
@Table(name = "utente")
public class Utente {


    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;


}
