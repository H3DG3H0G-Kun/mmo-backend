package ge.mmo.world.world;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A directed edge of the travel graph between two locations. */
@Entity
@Table(name = "location_link")
public class LocationLink {

    @Id
    private UUID id;

    @Column(name = "from_location_id", nullable = false)
    private UUID fromLocationId;

    @Column(name = "to_location_id", nullable = false)
    private UUID toLocationId;

    protected LocationLink() {
    }

    public UUID getFromLocationId() {
        return fromLocationId;
    }

    public UUID getToLocationId() {
        return toLocationId;
    }
}
