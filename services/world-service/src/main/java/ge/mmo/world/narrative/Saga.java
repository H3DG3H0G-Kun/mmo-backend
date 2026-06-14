package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Strand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A body of memory (a reign, a myth cycle, a whole book) grouping Tales. */
@Entity
@Table(name = "saga")
public class Saga {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Strand strand;

    @Column(name = "era_id", nullable = false)
    private Integer eraId;

    @Column(nullable = false)
    private int ordinal;

    protected Saga() {
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public Strand getStrand() {
        return strand;
    }

    public Integer getEraId() {
        return eraId;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
