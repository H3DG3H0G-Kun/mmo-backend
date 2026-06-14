package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.ConditionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** One AND-condition of a Tale's Resonance. All conditions must hold for the Tale to open. */
@Entity
@Table(name = "trigger_condition")
public class TriggerCondition {

    @Id
    private UUID id;

    @Column(name = "tale_id", nullable = false)
    private UUID taleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConditionType type;

    @Column(nullable = false)
    private String value;

    protected TriggerCondition() {
    }

    // Used by the content importer (same package).
    TriggerCondition(UUID id, UUID taleId, ConditionType type, String value) {
        this.id = id;
        this.taleId = taleId;
        this.type = type;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTaleId() {
        return taleId;
    }

    public ConditionType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
