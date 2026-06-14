package ge.mmo.world.economy;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.economy.EconomyViews.InventoryEntry;
import ge.mmo.world.economy.EconomyViews.InventoryView;
import ge.mmo.world.economy.EconomyViews.ListingView;
import ge.mmo.world.economy.ShopListing.Status;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The player-driven economy: gold wallets, inventories, gathering, and player shop listings —
 * the face-to-face social economy of the Lineage 2 world. Server-authoritative: the backend
 * moves items and gold, validates funds/ownership, and decides every transfer.
 */
@Service
public class EconomyService {

    static final long STARTING_GOLD = 100;
    static final long FORAGE_QUANTITY = 3;

    private final ItemDefRepository items;
    private final WalletRepository wallets;
    private final InventoryItemRepository inventory;
    private final ShopListingRepository listings;
    private final CharacterService characters;

    public EconomyService(ItemDefRepository items, WalletRepository wallets,
                          InventoryItemRepository inventory, ShopListingRepository listings,
                          CharacterService characters) {
        this.items = items;
        this.wallets = wallets;
        this.inventory = inventory;
        this.listings = listings;
        this.characters = characters;
    }

    @Transactional
    public InventoryView inventory(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        return inventoryView(characterId);
    }

    /** Gather a fixed amount of a random gatherable item (stands in for world gathering). */
    @Transactional
    public InventoryView forage(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        List<ItemDef> gatherables = items.findByGatherableTrue();
        if (gatherables.isEmpty()) {
            throw new NoGatherableItemsException();
        }
        ItemDef def = gatherables.get(ThreadLocalRandom.current().nextInt(gatherables.size()));
        addItem(characterId, def.getId(), FORAGE_QUANTITY);
        return inventoryView(characterId);
    }

    /** Browse the active market. */
    @Transactional(readOnly = true)
    public List<ListingView> market() {
        return listings.findByStatusOrderByCreatedAtDesc(Status.ACTIVE).stream()
                .map(this::listingView)
                .toList();
    }

    /** List a stack for sale (escrows it out of inventory until sold or cancelled). */
    @Transactional
    public ListingView listForSale(UUID accountId, UUID characterId, String itemCode, long quantity, long unitPrice) {
        characters.requireOwned(accountId, characterId);
        ItemDef def = items.findByCode(itemCode).orElseThrow(() -> new ItemNotFoundException(itemCode));
        removeItem(characterId, def.getId(), quantity);
        ShopListing listing = listings.save(
                new ShopListing(UUID.randomUUID(), characterId, def.getId(), quantity, unitPrice));
        return listingView(listing);
    }

    /** Buy a listing: gold moves buyer -> seller, the item moves to the buyer. */
    @Transactional
    public InventoryView buy(UUID accountId, UUID buyerCharacterId, UUID listingId) {
        characters.requireOwned(accountId, buyerCharacterId);
        ShopListing listing = listings.findById(listingId).orElseThrow(ListingNotFoundException::new);
        if (listing.getStatus() != Status.ACTIVE) {
            throw new ListingNotActiveException();
        }
        if (listing.getSellerCharacterId().equals(buyerCharacterId)) {
            throw new ListingOwnershipException("Cannot buy your own listing");
        }
        Wallet buyer = walletFor(buyerCharacterId);
        if (!buyer.debit(listing.total())) {
            throw new InsufficientGoldException();
        }
        Wallet seller = walletFor(listing.getSellerCharacterId());
        seller.credit(listing.total());
        wallets.save(buyer);
        wallets.save(seller);
        addItem(buyerCharacterId, listing.getItemDefId(), listing.getQuantity());
        listing.markSold();
        listings.save(listing);
        return inventoryView(buyerCharacterId);
    }

    /** Cancel your own listing; the escrowed stack returns to your inventory. */
    @Transactional
    public InventoryView cancel(UUID accountId, UUID characterId, UUID listingId) {
        characters.requireOwned(accountId, characterId);
        ShopListing listing = listings.findById(listingId).orElseThrow(ListingNotFoundException::new);
        if (!listing.getSellerCharacterId().equals(characterId)) {
            throw new ListingOwnershipException("Not your listing");
        }
        if (listing.getStatus() != Status.ACTIVE) {
            throw new ListingNotActiveException();
        }
        addItem(characterId, listing.getItemDefId(), listing.getQuantity());
        listing.markCancelled();
        listings.save(listing);
        return inventoryView(characterId);
    }

    // --- internals ---

    private Wallet walletFor(UUID characterId) {
        return wallets.findById(characterId)
                .orElseGet(() -> wallets.save(new Wallet(characterId, STARTING_GOLD)));
    }

    private void addItem(UUID characterId, UUID itemDefId, long qty) {
        inventory.findByCharacterIdAndItemDefId(characterId, itemDefId).ifPresentOrElse(
                stack -> {
                    stack.add(qty);
                    inventory.save(stack);
                },
                () -> inventory.save(new InventoryItem(UUID.randomUUID(), characterId, itemDefId, qty)));
    }

    private void removeItem(UUID characterId, UUID itemDefId, long qty) {
        InventoryItem stack = inventory.findByCharacterIdAndItemDefId(characterId, itemDefId)
                .orElseThrow(InsufficientItemsException::new);
        if (stack.getQuantity() < qty) {
            throw new InsufficientItemsException();
        }
        stack.remove(qty);
        if (stack.getQuantity() == 0) {
            inventory.delete(stack);
        } else {
            inventory.save(stack);
        }
    }

    private InventoryView inventoryView(UUID characterId) {
        List<InventoryItem> stacks = inventory.findByCharacterIdOrderByItemDefId(characterId);
        List<InventoryEntry> entries = stacks.stream()
                .map(s -> {
                    ItemDef def = items.findById(s.getItemDefId()).orElseThrow();
                    return new InventoryEntry(def.getCode(), def.getName(), s.getQuantity());
                })
                .toList();
        return new InventoryView(walletFor(characterId).getGold(), entries);
    }

    private ListingView listingView(ShopListing l) {
        ItemDef def = items.findById(l.getItemDefId()).orElseThrow();
        return new ListingView(l.getId(), l.getSellerCharacterId(), def.getCode(), def.getName(),
                l.getQuantity(), l.getUnitPrice(), l.total(), l.getStatus().name());
    }
}
