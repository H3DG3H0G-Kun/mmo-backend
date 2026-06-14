package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Tier;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** One self-contained narrative experience the Watcher enters — the design's "Thread". */
@Entity
@Table(name = "tale")
public class Tale {

    @Id
    private UUID id;

    @Column(name = "saga_id", nullable = false)
    private UUID sagaId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier;

    @Column(nullable = false)
    private int ordinal;

    /** Era opened to the Watcher upon completing this Tale (reward/unlock), or null. */
    @Column(name = "unlocks_era_id")
    private Integer unlocksEraId;

    protected Tale() {
    }

    // Used by the content importer (same package).
    Tale(UUID id, UUID sagaId, String code, String title, Tier tier, int ordinal, Integer unlocksEraId) {
        this.id = id;
        this.sagaId = sagaId;
        this.code = code;
        this.title = title;
        this.tier = tier;
        this.ordinal = ordinal;
        this.unlocksEraId = unlocksEraId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSagaId() {
        return sagaId;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public Tier getTier() {
        return tier;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public Integer getUnlocksEraId() {
        return unlocksEraId;
    }
}
