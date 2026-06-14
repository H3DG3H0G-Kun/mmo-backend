package ge.mmo.world.economy;

import java.util.List;
import java.util.UUID;

public final class EconomyViews {
    private EconomyViews() {
    }

    public record InventoryEntry(String itemCode, String itemName, long quantity) {
    }

    public record InventoryView(long gold, List<InventoryEntry> items) {
    }

    public record ListingView(
            UUID id,
            UUID sellerCharacterId,
            String itemCode,
            String itemName,
            long quantity,
            long unitPrice,
            long total,
            String status) {
    }
}
