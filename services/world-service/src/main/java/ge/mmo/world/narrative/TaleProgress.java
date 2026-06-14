package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Progress;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A Watcher's run through a Tale. */
@Entity
@Table(name = "tale_progress")
public class TaleProgress {

    @Id
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "tale_id", nullable = false)
    private UUID taleId;

    @Column(name = "current_beat_id")
    private UUID currentBeatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Progress status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;

    protected TaleProgress() {
    }

    public TaleProgress(UUID id, UUID characterId, UUID taleId, UUID currentBeatId) {
        this.id = id;
        this.characterId = characterId;
        this.taleId = taleId;
        this.currentBeatId = currentBeatId;
        this.status = Progress.IN_PROGRESS;
    }

    public void moveTo(UUID beatId) {
        this.currentBeatId = beatId;
    }

    public void complete() {
        this.status = Progress.COMPLETED;
        this.completedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public UUID getTaleId() {
        return taleId;
    }

    public UUID getCurrentBeatId() {
        return currentBeatId;
    }

    public Progress getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
