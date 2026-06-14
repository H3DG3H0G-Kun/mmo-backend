package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.combat.CombatService;
import ge.mmo.world.combat.CombatViews.EncounterView;
import ge.mmo.world.combat.CombatViews.EnemyView;
import ge.mmo.world.web.dto.CombatActionRequest;
import ge.mmo.world.web.dto.StartEncounterRequest;
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
@RequestMapping("/api/combat")
public class CombatController {

    private final CombatService combat;

    public CombatController(CombatService combat) {
        this.combat = combat;
    }

    @GetMapping("/enemies")
    public List<EnemyView> enemies() {
        return combat.enemies();
    }

    @PostMapping("/encounters")
    public ResponseEntity<EncounterView> start(@AuthenticationPrincipal AuthPrincipal principal,
                                               @Valid @RequestBody StartEncounterRequest req) {
        EncounterView view = combat.start(principal.accountId(), req.characterId(), req.enemyCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PostMapping("/encounters/{encounterId}/attack")
    public EncounterView attack(@AuthenticationPrincipal AuthPrincipal principal,
                                @PathVariable UUID encounterId,
                                @Valid @RequestBody CombatActionRequest req) {
        return combat.attack(principal.accountId(), req.characterId(), encounterId);
    }

    @GetMapping("/encounters/active")
    public EncounterView active(@AuthenticationPrincipal AuthPrincipal principal,
                                @RequestParam UUID characterId) {
        return combat.active(principal.accountId(), characterId);
    }
}
