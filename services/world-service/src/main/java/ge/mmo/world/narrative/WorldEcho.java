package ge.mmo.world.narrative;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A public, server-shared run of a Tale. The whole server contributes toward {@code goal}. */
@Entity
@Table(name = "world_echo")
public class WorldEcho {

    public enum Status { ACTIVE, RESOLVED }

    @Id
    private UUID id;

    @Column(name = "tale_id", nullable = false)
    private UUID taleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    private long goal;

    @Column(nullable = false)
    private long progress;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    protected WorldEcho() {
    }

    public WorldEcho(UUID id, UUID taleId, long goal) {
        this.id = id;
        this.taleId = taleId;
        this.goal = goal;
    }

    public void addProgress(long points) {
        this.progress += points;
    }

    public boolean goalReached() {
        return progress >= goal;
    }

    public void resolve() {
        this.status = Status.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTaleId() {
        return taleId;
    }

    public Status getStatus() {
        return status;
    }

    public long getGoal() {
        return goal;
    }

    public long getProgress() {
        return progress;
    }
}
