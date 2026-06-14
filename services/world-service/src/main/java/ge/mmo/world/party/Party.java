package ge.mmo.world.party;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "party")
public class Party {

    public enum Status { OPEN, CLOSED, DISBANDED }

    @Id
    private UUID id;

    @Column(name = "leader_character_id", nullable = false)
    private UUID leaderCharacterId;

    @Column(name = "max_size", nullable = false)
    private int maxSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Party() {
    }

    public Party(UUID id, UUID leaderCharacterId, int maxSize) {
        this.id = id;
        this.leaderCharacterId = leaderCharacterId;
        this.maxSize = maxSize;
    }

    public void promoteLeader(UUID characterId) {
        this.leaderCharacterId = characterId;
    }

    public void disband() {
        this.status = Status.DISBANDED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLeaderCharacterId() {
        return leaderCharacterId;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
