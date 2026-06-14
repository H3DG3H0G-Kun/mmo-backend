package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.siege.SiegeService;
import ge.mmo.world.siege.SiegeViews.PlaceView;
import ge.mmo.world.siege.SiegeViews.SiegeView;
import ge.mmo.world.web.dto.ContributeSiegeRequest;
import ge.mmo.world.web.dto.DeclareSiegeRequest;
import ge.mmo.world.web.dto.SiegeActorRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sieges")
public class SiegeController {

    private final SiegeService sieges;

    public SiegeController(SiegeService sieges) {
        this.sieges = sieges;
    }

    @GetMapping("/places")
    public List<PlaceView> places() {
        return sieges.listPlaces();
    }

    @GetMapping("/{siegeId}")
    public SiegeView get(@PathVariable UUID siegeId) {
        return sieges.getSiege(siegeId);
    }

    @PostMapping("/declare")
    public SiegeView declare(@AuthenticationPrincipal AuthPrincipal principal,
                             @Valid @RequestBody DeclareSiegeRequest req) {
        return sieges.declare(principal.accountId(), req.characterId(), req.placeCode());
    }

    @PostMapping("/{siegeId}/join")
    public SiegeView join(@AuthenticationPrincipal AuthPrincipal principal,
                          @PathVariable UUID siegeId,
                          @Valid @RequestBody SiegeActorRequest req) {
        return sieges.join(principal.accountId(), req.characterId(), siegeId);
    }

    @PostMapping("/{siegeId}/start")
    public SiegeView start(@AuthenticationPrincipal AuthPrincipal principal,
                           @PathVariable UUID siegeId,
                           @Valid @RequestBody SiegeActorRequest req) {
        return sieges.start(principal.accountId(), req.characterId(), siegeId);
    }

    @PostMapping("/{siegeId}/contribute")
    public SiegeView contribute(@AuthenticationPrincipal AuthPrincipal principal,
                                @PathVariable UUID siegeId,
                                @Valid @RequestBody ContributeSiegeRequest req) {
        return sieges.contribute(principal.accountId(), req.characterId(), siegeId, req.points());
    }

    @PostMapping("/{siegeId}/resolve")
    public SiegeView resolve(@AuthenticationPrincipal AuthPrincipal principal,
                             @PathVariable UUID siegeId,
                             @Valid @RequestBody SiegeActorRequest req) {
        return sieges.resolve(principal.accountId(), req.characterId(), siegeId);
    }
}
