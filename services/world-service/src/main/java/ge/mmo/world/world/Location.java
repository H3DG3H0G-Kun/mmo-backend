package ge.mmo.world.world;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A named place within an era. Its {@code code} matches the PLACE codes tales resonate at. */
@Entity
@Table(name = "location")
public class Location {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "era_id", nullable = false)
    private Integer eraId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    private String description;

    protected Location() {
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

    public LocationType getType() {
        return type;
    }

    public String getRegion() {
        return region;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getDescription() {
        return description;
    }
}
