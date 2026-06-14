package ge.mmo.world.narrative;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * A party's shared run of a Tale. Progress lives here (one current beat for the whole party);
 * on completion the reward fans out to every member. Solo runs still use {@link TaleProgress}.
 */
@Entity
@Table(name = "tale_instance")
public class TaleInstance {

    public enum Status { ACTIVE, COMPLETED }

    @Id
    private UUID id;

    @Column(name = "tale_id", nullable = false)
    private UUID taleId;

    @Column(name = "party_id", nullable = false)
    private UUID partyId;

    @Column(name = "current_beat_id")
    private UUID currentBeatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;

    protected TaleInstance() {
    }

    public TaleInstance(UUID id, UUID taleId, UUID partyId, UUID currentBeatId) {
        this.id = id;
        this.taleId = taleId;
        this.partyId = partyId;
        this.currentBeatId = currentBeatId;
    }

    public void moveTo(UUID beatId) {
        this.currentBeatId = beatId;
    }

    public void complete() {
        this.status = Status.COMPLETED;
        this.completedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTaleId() {
        return taleId;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getCurrentBeatId() {
        return currentBeatId;
    }

    public Status getStatus() {
        return status;
    }
}
