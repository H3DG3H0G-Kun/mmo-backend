package ge.mmo.world.clan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan")
public class Clan {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String tag;

    @Column(name = "leader_character_id", nullable = false)
    private UUID leaderCharacterId;

    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Clan() {
    }

    public Clan(UUID id, String name, String tag, UUID leaderCharacterId, int maxMembers) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.leaderCharacterId = leaderCharacterId;
        this.maxMembers = maxMembers;
    }

    public void setLeader(UUID characterId) {
        this.leaderCharacterId = characterId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public UUID getLeaderCharacterId() {
        return leaderCharacterId;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
