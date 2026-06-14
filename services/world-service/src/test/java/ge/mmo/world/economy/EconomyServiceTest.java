package ge.mmo.world.economy;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EconomyServiceTest {

    private ItemDefRepository items;
    private WalletRepository wallets;
    private InventoryItemRepository inventory;
    private ShopListingRepository listings;
    private CharacterService characters;
    private EconomyService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID buyer = UUID.randomUUID();
    private final UUID seller = UUID.randomUUID();
    private final UUID defId = UUID.randomUUID();
    private final UUID listingId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        items = mock(ItemDefRepository.class);
        wallets = mock(WalletRepository.class);
        inventory = mock(InventoryItemRepository.class);
        listings = mock(ShopListingRepository.class);
        characters = mock(CharacterService.class);
        service = new EconomyService(items, wallets, inventory, listings, characters);
        when(characters.requireOwned(any(), any())).thenReturn(mock(PlayerCharacter.class));
        ItemDef def = mock(ItemDef.class);
        when(def.getCode()).thenReturn("MOUNTAIN_HERB");
        when(def.getName()).thenReturn("Mountain Herb");
        when(items.findById(defId)).thenReturn(Optional.of(def));
    }

    @Test
    void buyMovesGoldAndItem() {
        ShopListing listing = new ShopListing(listingId, seller, defId, 2, 10); // total 20
        Wallet buyerWallet = new Wallet(buyer, 100);
        Wallet sellerWallet = new Wallet(seller, 0);
        when(listings.findById(listingId)).thenReturn(Optional.of(listing));
        when(wallets.findById(buyer)).thenReturn(Optional.of(buyerWallet));
        when(wallets.findById(seller)).thenReturn(Optional.of(sellerWallet));
        when(inventory.findByCharacterIdAndItemDefId(buyer, defId)).thenReturn(Optional.empty());
        when(inventory.findByCharacterIdOrderByItemDefId(buyer))
                .thenReturn(List.of(new InventoryItem(UUID.randomUUID(), buyer, defId, 2)));

        var view = service.buy(acc, buyer, listingId);

        assertThat(buyerWallet.getGold()).isEqualTo(80);
        assertThat(sellerWallet.getGold()).isEqualTo(20);
        assertThat(listing.getStatus()).isEqualTo(ShopListing.Status.SOLD);
        assertThat(view.gold()).isEqualTo(80);
        assertThat(view.items()).anyMatch(e -> e.itemCode().equals("MOUNTAIN_HERB") && e.quantity() == 2);
    }

    @Test
    void cannotBuyOwnListing() {
        ShopListing listing = new ShopListing(listingId, buyer, defId, 2, 10);
        when(listings.findById(listingId)).thenReturn(Optional.of(listing));
        assertThatThrownBy(() -> service.buy(acc, buyer, listingId))
                .isInstanceOf(ListingOwnershipException.class);
    }

    @Test
    void buyWithInsufficientGoldIsRejected() {
        ShopListing listing = new ShopListing(listingId, seller, defId, 2, 10); // total 20
        when(listings.findById(listingId)).thenReturn(Optional.of(listing));
        when(wallets.findById(buyer)).thenReturn(Optional.of(new Wallet(buyer, 5)));
        assertThatThrownBy(() -> service.buy(acc, buyer, listingId))
                .isInstanceOf(InsufficientGoldException.class);
    }

    @Test
    void listingMoreThanOwnedIsRejected() {
        ItemDef def = mock(ItemDef.class);
        when(def.getId()).thenReturn(defId);
        when(items.findByCode("MOUNTAIN_HERB")).thenReturn(Optional.of(def));
        when(inventory.findByCharacterIdAndItemDefId(seller, defId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listForSale(acc, seller, "MOUNTAIN_HERB", 5, 10))
                .isInstanceOf(InsufficientItemsException.class);
    }
}
