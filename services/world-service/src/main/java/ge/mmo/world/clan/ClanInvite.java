package ge.mmo.world.clan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clan_invite")
public class ClanInvite {

    @Id
    private UUID id;

    @Column(name = "clan_id", nullable = false)
    private UUID clanId;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "invited_by", nullable = false)
    private UUID invitedBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected ClanInvite() {
    }

    public ClanInvite(UUID id, UUID clanId, UUID characterId, UUID invitedBy) {
        this.id = id;
        this.clanId = clanId;
        this.characterId = characterId;
        this.invitedBy = invitedBy;
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

    public UUID getInvitedBy() {
        return invitedBy;
    }
}
