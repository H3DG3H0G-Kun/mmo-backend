package ge.mmo.world.economy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** A player's offer to sell a stack of an item at a unit price. */
@Entity
@Table(name = "shop_listing")
public class ShopListing {

    public enum Status { ACTIVE, SOLD, CANCELLED }

    @Id
    private UUID id;

    @Column(name = "seller_character_id", nullable = false)
    private UUID sellerCharacterId;

    @Column(name = "item_def_id", nullable = false)
    private UUID itemDefId;

    @Column(nullable = false)
    private long quantity;

    @Column(name = "unit_price", nullable = false)
    private long unitPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected ShopListing() {
    }

    public ShopListing(UUID id, UUID sellerCharacterId, UUID itemDefId, long quantity, long unitPrice) {
        this.id = id;
        this.sellerCharacterId = sellerCharacterId;
        this.itemDefId = itemDefId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public long total() {
        return quantity * unitPrice;
    }

    public void markSold() {
        this.status = Status.SOLD;
    }

    public void markCancelled() {
        this.status = Status.CANCELLED;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSellerCharacterId() {
        return sellerCharacterId;
    }

    public UUID getItemDefId() {
        return itemDefId;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public Status getStatus() {
        return status;
    }
}
