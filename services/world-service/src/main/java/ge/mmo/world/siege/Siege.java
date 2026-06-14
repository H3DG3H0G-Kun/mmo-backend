package ge.mmo.world.siege;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "siege")
public class Siege {

    public enum Status { DECLARED, ACTIVE, RESOLVED }

    @Id
    private UUID id;

    @Column(name = "place_id", nullable = false)
    private UUID placeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DECLARED;

    @Column(name = "declaring_clan_id", nullable = false)
    private UUID declaringClanId;

    @Column(name = "winner_clan_id")
    private UUID winnerClanId;

    @Column(name = "declared_at", nullable = false)
    private Instant declaredAt = Instant.now();

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    protected Siege() {
    }

    public Siege(UUID id, UUID placeId, UUID declaringClanId) {
        this.id = id;
        this.placeId = placeId;
        this.declaringClanId = declaringClanId;
    }

    public void activate() {
        this.status = Status.ACTIVE;
        this.startedAt = Instant.now();
    }

    public void resolve(UUID winnerClanId) {
        this.status = Status.RESOLVED;
        this.winnerClanId = winnerClanId;
        this.resolvedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPlaceId() {
        return placeId;
    }

    public Status getStatus() {
        return status;
    }

    public UUID getDeclaringClanId() {
        return declaringClanId;
    }

    public UUID getWinnerClanId() {
        return winnerClanId;
    }
}
