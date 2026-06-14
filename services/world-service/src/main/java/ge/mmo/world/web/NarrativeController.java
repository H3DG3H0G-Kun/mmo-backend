package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.narrative.InstanceService;
import ge.mmo.world.narrative.NarrativeService;
import ge.mmo.world.narrative.NarrativeViews.ResonanceView;
import ge.mmo.world.narrative.NarrativeViews.TaleStateView;
import ge.mmo.world.narrative.NarrativeViews.WorldEchoView;
import ge.mmo.world.narrative.WorldEchoService;
import ge.mmo.world.web.dto.AdvanceTaleRequest;
import ge.mmo.world.web.dto.EchoContributeRequest;
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
    private final InstanceService instances;
    private final WorldEchoService echoes;

    public NarrativeController(NarrativeService narrative, InstanceService instances, WorldEchoService echoes) {
        this.narrative = narrative;
        this.instances = instances;
        this.echoes = echoes;
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

    // --- Co-op (party-shared) runs ---

    /** Party leader starts a shared run of a Tale (validated by the leader's Resonance). */
    @PostMapping("/party/tales/{taleCode}/start")
    public TaleStateView startParty(@AuthenticationPrincipal AuthPrincipal principal,
                                    @PathVariable String taleCode,
                                    @Valid @RequestBody EnterTaleRequest req) {
        return instances.start(principal.accountId(), req.characterId(), taleCode, req.place(), req.states());
    }

    /** Any party member advances the shared beat; completion rewards the whole party. */
    @PostMapping("/party/advance")
    public TaleStateView advanceParty(@AuthenticationPrincipal AuthPrincipal principal,
                                      @Valid @RequestBody AdvanceTaleRequest req) {
        return instances.advance(principal.accountId(), req.characterId(), req.choiceKey());
    }

    /** The party's current shared run state. */
    @GetMapping("/party/instance")
    public TaleStateView partyInstance(@AuthenticationPrincipal AuthPrincipal principal,
                                       @RequestParam UUID characterId) {
        return instances.stateForParty(principal.accountId(), characterId);
    }

    // --- World Echoes (public, server-shared) ---

    /** Summon a public Echo of a Tale (validated by the summoner's Resonance). */
    @PostMapping("/echoes/tales/{taleCode}/summon")
    public WorldEchoView summonEcho(@AuthenticationPrincipal AuthPrincipal principal,
                                    @PathVariable String taleCode,
                                    @Valid @RequestBody EnterTaleRequest req) {
        return echoes.summon(principal.accountId(), req.characterId(), taleCode, req.place(), req.states());
    }

    /** Contribute to a public Echo; reaching its goal restores it for all participants. */
    @PostMapping("/echoes/{echoId}/contribute")
    public WorldEchoView contributeEcho(@AuthenticationPrincipal AuthPrincipal principal,
                                        @PathVariable UUID echoId,
                                        @Valid @RequestBody EchoContributeRequest req) {
        return echoes.contribute(principal.accountId(), req.characterId(), echoId, req.points());
    }

    @GetMapping("/echoes")
    public List<WorldEchoView> activeEchoes() {
        return echoes.listActive();
    }

    @GetMapping("/echoes/{echoId}")
    public WorldEchoView echo(@PathVariable UUID echoId) {
        return echoes.get(echoId);
    }
}
