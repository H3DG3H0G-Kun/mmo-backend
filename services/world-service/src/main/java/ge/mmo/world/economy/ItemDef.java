package ge.mmo.world.economy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** Catalogue definition of an item (content as data). */
@Entity
@Table(name = "item_def")
public class ItemDef {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean stackable;

    @Column(nullable = false)
    private boolean gatherable;

    protected ItemDef() {
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

    public String getDescription() {
        return description;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isGatherable() {
        return gatherable;
    }
}
