package ge.mmo.auth.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "account")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String username;

    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Comma-separated role names, e.g. {@code "PLAYER"} or {@code "PLAYER,ADMIN"}. */
    @Column(nullable = false)
    private String roles = "PLAYER";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Account() {
        // for JPA
    }

    public Account(UUID id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public List<String> roleList() {
        return List.of(roles.split(","));
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
