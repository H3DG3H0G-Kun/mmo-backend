package ge.mmo.world.timeline;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** Records that a character has unlocked access to an era (a Tale-completion reward). */
@Entity
@Table(name = "character_era_unlock")
public class EraUnlock {

    @Id
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "era_id", nullable = false)
    private Integer eraId;

    @Column(name = "unlocked_at", nullable = false)
    private Instant unlockedAt = Instant.now();

    protected EraUnlock() {
    }

    public EraUnlock(UUID id, UUID characterId, Integer eraId) {
        this.id = id;
        this.characterId = characterId;
        this.eraId = eraId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public Integer getEraId() {
        return eraId;
    }

    public Instant getUnlockedAt() {
        return unlockedAt;
    }
}
