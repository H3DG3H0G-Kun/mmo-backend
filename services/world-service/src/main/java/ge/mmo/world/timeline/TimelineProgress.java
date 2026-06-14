package ge.mmo.world.timeline;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Server-wide count of memories restored in an era — the Living Timeline. */
@Entity
@Table(name = "timeline_progress")
public class TimelineProgress {

    @Id
    @Column(name = "era_id")
    private Integer eraId;

    @Column(name = "restored_count", nullable = false)
    private long restoredCount;

    protected TimelineProgress() {
    }

    public void incrementRestored() {
        this.restoredCount++;
    }

    public Integer getEraId() {
        return eraId;
    }

    public long getRestoredCount() {
        return restoredCount;
    }
}
