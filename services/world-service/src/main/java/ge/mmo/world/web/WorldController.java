package ge.mmo.world.web;

import ge.mmo.common.security.AuthPrincipal;
import ge.mmo.world.timeline.TimelineService;
import ge.mmo.world.timeline.TimelineViews.EraStatusView;
import ge.mmo.world.timeline.TimelineViews.TimelineEraView;
import ge.mmo.world.web.dto.CharacterResponse;
import ge.mmo.world.web.dto.TravelRequest;
import ge.mmo.world.web.dto.TravelToRequest;
import ge.mmo.world.world.LocationService;
import ge.mmo.world.world.LocationViews.LocationsResponse;
import ge.mmo.world.world.NpcService;
import ge.mmo.world.world.NpcService.NpcView;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/world")
public class WorldController {

    private final TimelineService timeline;
    private final LocationService locations;
    private final NpcService npcs;

    public WorldController(TimelineService timeline, LocationService locations, NpcService npcs) {
        this.timeline = timeline;
        this.locations = locations;
        this.npcs = npcs;
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

    /** Locations in the character's current era, flagged by reachability from where they stand. */
    @GetMapping("/locations")
    public LocationsResponse locations(@AuthenticationPrincipal AuthPrincipal principal,
                                       @RequestParam UUID characterId) {
        return locations.forCharacter(principal.accountId(), characterId);
    }

    /** All locations of a named era (for maps/browsing). */
    @GetMapping("/locations/era/{eraCode}")
    public LocationsResponse locationsByEra(@PathVariable String eraCode) {
        return locations.forEra(eraCode);
    }

    /** Travel within the current era to a connected location. */
    @PostMapping("/travel-to")
    public LocationsResponse travelTo(@AuthenticationPrincipal AuthPrincipal principal,
                                      @Valid @RequestBody TravelToRequest req) {
        return locations.travelTo(principal.accountId(), req.characterId(), req.locationCode());
    }

    /** NPCs present in the character's current era (also delivered via the WS presence stream). */
    @GetMapping("/npcs")
    public List<NpcView> npcs(@AuthenticationPrincipal AuthPrincipal principal,
                             @RequestParam UUID characterId) {
        return npcs.forCharacter(principal.accountId(), characterId);
    }
}
