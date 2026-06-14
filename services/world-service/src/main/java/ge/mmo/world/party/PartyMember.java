package ge.mmo.world.party;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "party_member")
public class PartyMember {

    @Id
    private UUID id;

    @Column(name = "party_id", nullable = false)
    private UUID partyId;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    protected PartyMember() {
    }

    public PartyMember(UUID id, UUID partyId, UUID characterId) {
        this.id = id;
        this.partyId = partyId;
        this.characterId = characterId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPartyId() {
        return partyId;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }
}
