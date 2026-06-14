package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Interaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** An ordered scene within a Tale, exposing one Interaction the Watcher may take. */
@Entity
@Table(name = "beat")
public class Beat {

    @Id
    private UUID id;

    @Column(name = "tale_id", nullable = false)
    private UUID taleId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int ordinal;

    @Column(nullable = false)
    private String narration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Interaction interaction;

    @Column(nullable = false)
    private boolean terminal;

    protected Beat() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getTaleId() {
        return taleId;
    }

    public String getCode() {
        return code;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public String getNarration() {
        return narration;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public boolean isTerminal() {
        return terminal;
    }
}
