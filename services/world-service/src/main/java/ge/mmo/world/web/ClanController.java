package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.clan.ClanService;
import ge.mmo.world.clan.ClanViews.ClanView;
import ge.mmo.world.web.dto.ClanMemberActionRequest;
import ge.mmo.world.web.dto.ClanSelfRequest;
import ge.mmo.world.web.dto.CreateClanRequest;
import ge.mmo.world.web.dto.SetClanRankRequest;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/clans")
public class ClanController {

    private final ClanService clans;

    public ClanController(ClanService clans) {
        this.clans = clans;
    }

    @PostMapping
    public ResponseEntity<ClanView> create(@AuthenticationPrincipal AuthPrincipal principal,
                                           @Valid @RequestBody CreateClanRequest req) {
        ClanView view = clans.create(principal.accountId(), req.characterId(), req.name(), req.tag(), req.maxMembers());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PostMapping("/{clanId}/invite")
    public ClanView invite(@AuthenticationPrincipal AuthPrincipal principal,
                           @PathVariable UUID clanId,
                           @Valid @RequestBody ClanMemberActionRequest req) {
        // clanId is implied by the actor's membership; kept in the path for clarity/REST shape.
        return clans.invite(principal.accountId(), req.characterId(), req.targetCharacterId());
    }

    @PostMapping("/{clanId}/accept")
    public ClanView accept(@AuthenticationPrincipal AuthPrincipal principal,
                           @PathVariable UUID clanId,
                           @Valid @RequestBody ClanSelfRequest req) {
        return clans.accept(principal.accountId(), req.characterId(), clanId);
    }

    @PostMapping("/leave")
    public ResponseEntity<ClanView> leave(@AuthenticationPrincipal AuthPrincipal principal,
                                          @Valid @RequestBody ClanSelfRequest req) {
        return clans.leave(principal.accountId(), req.characterId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build()); // clan disbanded (last member left)
    }

    @PostMapping("/kick")
    public ClanView kick(@AuthenticationPrincipal AuthPrincipal principal,
                         @Valid @RequestBody ClanMemberActionRequest req) {
        return clans.kick(principal.accountId(), req.characterId(), req.targetCharacterId());
    }

    @PostMapping("/rank")
    public ClanView setRank(@AuthenticationPrincipal AuthPrincipal principal,
                            @Valid @RequestBody SetClanRankRequest req) {
        return clans.setRank(principal.accountId(), req.characterId(), req.targetCharacterId(), req.rank());
    }

    @GetMapping
    public ClanView mine(@AuthenticationPrincipal AuthPrincipal principal,
                         @RequestParam UUID characterId) {
        return clans.myClan(principal.accountId(), characterId);
    }
}
