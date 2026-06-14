package ge.mmo.world.clan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan_member")
public class ClanMember {

    @Id
    private UUID id;

    @Column(name = "clan_id", nullable = false)
    private UUID clanId;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClanRank rank;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    protected ClanMember() {
    }

    public ClanMember(UUID id, UUID clanId, UUID characterId, ClanRank rank) {
        this.id = id;
        this.clanId = clanId;
        this.characterId = characterId;
        this.rank = rank;
    }

    public void setRank(ClanRank rank) {
        this.rank = rank;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClanId() {
        return clanId;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public ClanRank getRank() {
        return rank;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}
