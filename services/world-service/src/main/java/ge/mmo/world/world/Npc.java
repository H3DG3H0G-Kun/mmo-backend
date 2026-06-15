package ge.mmo.world.world;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A server-spawned NPC (vendor, herald, townsfolk). Appears in the presence stream as kind=NPC. */
@Entity
@Table(name = "npc")
public class Npc {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(name = "era_id", nullable = false)
    private Integer eraId;

    @Column(name = "location_code", nullable = false)
    private String locationCode;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    protected Npc() {
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

    public String getRole() {
        return role;
    }

    public Integer getEraId() {
        return eraId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
