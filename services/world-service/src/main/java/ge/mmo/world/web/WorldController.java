package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.timeline.TimelineService;
import ge.mmo.world.timeline.TimelineViews.EraStatusView;
import ge.mmo.world.timeline.TimelineViews.TimelineEraView;
import ge.mmo.world.web.dto.CharacterResponse;
import ge.mmo.world.web.dto.TravelRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/world")
public class WorldController {

    private final TimelineService timeline;

    public WorldController(TimelineService timeline) {
        this.timeline = timeline;
    }

    /** All eras with this character's unlock status. */
    @GetMapping("/eras")
    public List<EraStatusView> eras(@AuthenticationPrincipal AuthPrincipal principal,
                                    @RequestParam UUID characterId) {
        return timeline.listEras(principal.accountId(), characterId);
    }

    /** Travel a character to another era (must be unlocked). */
    @PostMapping("/travel")
    public CharacterResponse travel(@AuthenticationPrincipal AuthPrincipal principal,
                                    @Valid @RequestBody TravelRequest req) {
        return CharacterResponse.of(timeline.travel(principal.accountId(), req.characterId(), req.eraCode()));
    }

    /** The server-wide Living Timeline: memories restored per era. */
    @GetMapping("/timeline")
    public List<TimelineEraView> timeline() {
        return timeline.livingTimeline();
    }
}
