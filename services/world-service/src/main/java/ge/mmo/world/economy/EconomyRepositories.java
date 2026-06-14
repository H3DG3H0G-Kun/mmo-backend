package ge.mmo.world.economy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ItemDefRepository extends JpaRepository<ItemDef, UUID> {
    Optional<ItemDef> findByCode(String code);

    List<ItemDef> findByGatherableTrue();
}

interface WalletRepository extends JpaRepository<Wallet, UUID> {
}

interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Optional<InventoryItem> findByCharacterIdAndItemDefId(UUID characterId, UUID itemDefId);

    List<InventoryItem> findByCharacterIdOrderByItemDefId(UUID characterId);
}

interface ShopListingRepository extends JpaRepository<ShopListing, UUID> {
    List<ShopListing> findByStatusOrderByCreatedAtDesc(ShopListing.Status status);
}
