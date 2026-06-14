package ge.mmo.world.economy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** A stack of one item type held by a character. */
@Entity
@Table(name = "inventory_item")
public class InventoryItem {

    @Id
    private UUID id;

    @Column(name = "character_id", nullable = false)
    private UUID characterId;

    @Column(name = "item_def_id", nullable = false)
    private UUID itemDefId;

    @Column(nullable = false)
    private long quantity;

    protected InventoryItem() {
    }

    public InventoryItem(UUID id, UUID characterId, UUID itemDefId, long quantity) {
        this.id = id;
        this.characterId = characterId;
        this.itemDefId = itemDefId;
        this.quantity = quantity;
    }

    public void add(long amount) {
        this.quantity += amount;
    }

    public void remove(long amount) {
        this.quantity -= amount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCharacterId() {
        return characterId;
    }

    public UUID getItemDefId() {
        return itemDefId;
    }

    public long getQuantity() {
        return quantity;
    }
}
