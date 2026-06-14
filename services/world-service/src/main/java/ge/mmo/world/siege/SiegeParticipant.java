package ge.mmo.world.siege;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A clan contesting a siege, with its accumulated contribution score. */
@Entity
@Table(name = "siege_participant")
public class SiegeParticipant {

    @Id
    private UUID id;

    @Column(name = "siege_id", nullable = false)
    private UUID siegeId;

    @Column(name = "clan_id", nullable = false)
    private UUID clanId;

    @Column(nullable = false)
    private long score;

    protected SiegeParticipant() {
    }

    public SiegeParticipant(UUID id, UUID siegeId, UUID clanId) {
        this.id = id;
        this.siegeId = siegeId;
        this.clanId = clanId;
    }

    public void addScore(long points) {
        this.score += points;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSiegeId() {
        return siegeId;
    }

    public UUID getClanId() {
        return clanId;
    }

    public long getScore() {
        return score;
    }
}
