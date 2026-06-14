package ge.mmo.world.character;

import ge.mmo.world.world.Era;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A player's character — a Watcher soul — owned by an auth account. */
@Entity
@Table(name = "player_character")
public class PlayerCharacter {

    @Id
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "current_era_id", nullable = false)
    private Era currentEra;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected PlayerCharacter() {
    }

    public PlayerCharacter(UUID id, UUID accountId, String name, Era currentEra) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.currentEra = currentEra;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public Era getCurrentEra() {
        return currentEra;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
