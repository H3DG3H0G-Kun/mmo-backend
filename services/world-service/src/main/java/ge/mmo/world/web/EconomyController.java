package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.economy.EconomyService;
import ge.mmo.world.economy.EconomyViews.InventoryView;
import ge.mmo.world.economy.EconomyViews.ListingView;
import ge.mmo.world.web.dto.ListForSaleRequest;
import ge.mmo.world.web.dto.ShopActionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/economy")
public class EconomyController {

    private final EconomyService economy;

    public EconomyController(EconomyService economy) {
        this.economy = economy;
    }

    @GetMapping("/inventory")
    public InventoryView inventory(@AuthenticationPrincipal AuthPrincipal principal,
                                   @RequestParam UUID characterId) {
        return economy.inventory(principal.accountId(), characterId);
    }

    @PostMapping("/forage")
    public InventoryView forage(@AuthenticationPrincipal AuthPrincipal principal,
                                @Valid @RequestBody ShopActionRequest req) {
        return economy.forage(principal.accountId(), req.characterId());
    }

    @GetMapping("/market")
    public List<ListingView> market() {
        return economy.market();
    }

    @PostMapping("/listings")
    public ResponseEntity<ListingView> list(@AuthenticationPrincipal AuthPrincipal principal,
                                            @Valid @RequestBody ListForSaleRequest req) {
        ListingView view = economy.listForSale(
                principal.accountId(), req.characterId(), req.itemCode(), req.quantity(), req.unitPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PostMapping("/listings/{listingId}/buy")
    public InventoryView buy(@AuthenticationPrincipal AuthPrincipal principal,
                             @PathVariable UUID listingId,
                             @Valid @RequestBody ShopActionRequest req) {
        return economy.buy(principal.accountId(), req.characterId(), listingId);
    }

    @PostMapping("/listings/{listingId}/cancel")
    public InventoryView cancel(@AuthenticationPrincipal AuthPrincipal principal,
                                @PathVariable UUID listingId,
                                @Valid @RequestBody ShopActionRequest req) {
        return economy.cancel(principal.accountId(), req.characterId(), listingId);
    }
}
