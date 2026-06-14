package ge.mmo.world.world;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A time-layer of the persistent shared History Layer. */
@Entity
@Table(name = "era")
public class Era {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int ordinal;

    @Column(name = "default_unlocked", nullable = false)
    private boolean defaultUnlocked;

    protected Era() {
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public boolean isDefaultUnlocked() {
        return defaultUnlocked;
    }
}
