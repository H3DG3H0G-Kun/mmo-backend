package ge.mmo.world.narrative;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A directed edge in a Tale's beat graph. {@code choiceKey} is set only out of CHOOSE beats. */
@Entity
@Table(name = "beat_edge")
public class BeatEdge {

    @Id
    private UUID id;

    @Column(name = "from_beat_id", nullable = false)
    private UUID fromBeatId;

    @Column(name = "to_beat_id", nullable = false)
    private UUID toBeatId;

    @Column(name = "choice_key")
    private String choiceKey;

    protected BeatEdge() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getFromBeatId() {
        return fromBeatId;
    }

    public UUID getToBeatId() {
        return toBeatId;
    }

    public String getChoiceKey() {
        return choiceKey;
    }
}
