package ge.mmo.world.narrative;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** One Watcher's contribution to a World Echo. */
@Entity
@Table(name = "world_echo_participation")
public class WorldEchoParticipation {

    @Id
    private UUID id;

    @Column(name = "world_echo_id", nullable = false)
    private UUID worldEchoId;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(nullable = false)
    private long contribution;

    protected WorldEchoParticipation() {
    }

    public WorldEchoParticipation(UUID id, UUID worldEchoId, UUID characterId) {
        this.id = id;
        this.worldEchoId = worldEchoId;
        this.characterId = characterId;
    }

    public void addContribution(long points) {
        this.contribution += points;
    }

    public UUID getId() {
        return id;
    }

    public UUID getWorldEchoId() {
        return worldEchoId;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public long getContribution() {
        return contribution;
    }
}
