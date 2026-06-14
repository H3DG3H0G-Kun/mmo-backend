package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.party.PartyService;
import ge.mmo.world.party.PartyViews.PartyView;
import ge.mmo.world.web.dto.JoinPartyRequest;
import ge.mmo.world.web.dto.PartyActionRequest;
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
@RequestMapping("/api/party")
public class PartyController {

    private final PartyService parties;

    public PartyController(PartyService parties) {
        this.parties = parties;
    }

    @PostMapping
    public ResponseEntity<PartyView> create(@AuthenticationPrincipal AuthPrincipal principal,
                                            @Valid @RequestBody PartyActionRequest req) {
        PartyView view = parties.create(principal.accountId(), req.characterId(), req.maxSize());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PostMapping("/{partyId}/join")
    public PartyView join(@AuthenticationPrincipal AuthPrincipal principal,
                          @PathVariable UUID partyId,
                          @Valid @RequestBody JoinPartyRequest req) {
        return parties.join(principal.accountId(), req.characterId(), partyId);
    }

    @PostMapping("/leave")
    public ResponseEntity<PartyView> leave(@AuthenticationPrincipal AuthPrincipal principal,
                                           @Valid @RequestBody JoinPartyRequest req) {
        return parties.leave(principal.accountId(), req.characterId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build()); // party disbanded (was last member)
    }

    @PostMapping("/disband")
    public ResponseEntity<Void> disband(@AuthenticationPrincipal AuthPrincipal principal,
                                        @Valid @RequestBody JoinPartyRequest req) {
        parties.disband(principal.accountId(), req.characterId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PartyView mine(@AuthenticationPrincipal AuthPrincipal principal,
                          @RequestParam UUID characterId) {
        return parties.myParty(principal.accountId(), characterId);
    }
}
