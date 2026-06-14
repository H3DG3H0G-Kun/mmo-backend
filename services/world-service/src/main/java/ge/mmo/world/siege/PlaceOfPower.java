package ge.mmo.world.siege;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A contestable territory/fortress. Held by a clan (or unclaimed). */
@Entity
@Table(name = "place_of_power")
public class PlaceOfPower {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "era_id", nullable = false)
    private Integer eraId;

    @Column(name = "holder_clan_id")
    private UUID holderClanId;

    @Column(name = "captured_at")
    private Instant capturedAt;

    protected PlaceOfPower() {
    }

    public void captureBy(UUID clanId) {
        this.holderClanId = clanId;
        this.capturedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getEraId() {
        return eraId;
    }

    public UUID getHolderClanId() {
        return holderClanId;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }
}
