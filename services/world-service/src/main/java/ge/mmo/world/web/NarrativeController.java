package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.narrative.NarrativeService;
import ge.mmo.world.narrative.NarrativeViews.ResonanceView;
import ge.mmo.world.narrative.NarrativeViews.TaleStateView;
import ge.mmo.world.web.dto.AdvanceTaleRequest;
import ge.mmo.world.web.dto.EnterTaleRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/narrative")
public class NarrativeController {

    private final NarrativeService narrative;

    public NarrativeController(NarrativeService narrative) {
        this.narrative = narrative;
    }

    /** Tales whose Resonance is open for the character here and now (the Heralds present). */
    @GetMapping("/resonances")
    public List<ResonanceView> resonances(@AuthenticationPrincipal AuthPrincipal principal,
                                          @RequestParam UUID characterId,
                                          @RequestParam(required = false) String place,
                                          @RequestParam(required = false) Set<String> states) {
        return narrative.availableResonances(principal.accountId(), characterId, place, states);
    }

    @PostMapping("/tales/{taleCode}/enter")
    public TaleStateView enter(@AuthenticationPrincipal AuthPrincipal principal,
                               @PathVariable String taleCode,
                               @Valid @RequestBody EnterTaleRequest req) {
        return narrative.enter(principal.accountId(), req.characterId(), taleCode, req.place(), req.states());
    }

    @PostMapping("/tales/{taleCode}/advance")
    public TaleStateView advance(@AuthenticationPrincipal AuthPrincipal principal,
                                 @PathVariable String taleCode,
                                 @Valid @RequestBody AdvanceTaleRequest req) {
        return narrative.advance(principal.accountId(), req.characterId(), taleCode, req.choiceKey());
    }
}
