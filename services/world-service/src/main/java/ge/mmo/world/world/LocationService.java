package ge.mmo.world.world;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.world.LocationViews.LocationView;
import ge.mmo.world.world.LocationViews.LocationsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The world-geography layer: which Locations exist in an era, the travel graph between them, and a
 * character's current location. Intra-era travel is backend-validated (you may only move to a
 * connected location in your own era; era travel is a separate concern in TimelineService).
 */
@Service
public class LocationService {

    private final LocationRepository locations;
    private final LocationLinkRepository links;
    private final EraRepository eras;
    private final CharacterService characters;

    public LocationService(LocationRepository locations, LocationLinkRepository links,
                           EraRepository eras, CharacterService characters) {
        this.locations = locations;
        this.links = links;
        this.eras = eras;
        this.characters = characters;
    }

    /** Locations in the character's current era, flagged by reachability from where they stand. */
    @Transactional(readOnly = true)
    public LocationsResponse forCharacter(UUID accountId, UUID characterId) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        Era era = character.getCurrentEra();
        List<Location> inEra = locations.findByEraIdOrderByName(era.getId());
        UUID currentId = character.getCurrentLocationId();

        Set<UUID> reachable = currentId == null ? Set.of()
                : links.findByFromLocationId(currentId).stream()
                .map(LocationLink::getToLocationId)
                .collect(Collectors.toSet());

        String currentCode = inEra.stream()
                .filter(l -> l.getId().equals(currentId))
                .map(Location::getCode)
                .findFirst().orElse(null);

        List<LocationView> views = inEra.stream()
                .map(l -> LocationView.of(l, currentId == null || l.getId().equals(currentId)
                        || reachable.contains(l.getId())))
                .toList();
        return new LocationsResponse(era.getCode(), currentCode, views);
    }

    /** All locations of a named era (no character context); for browsing/maps. */
    @Transactional(readOnly = true)
    public LocationsResponse forEra(String eraCode) {
        Era era = eras.findByCode(eraCode).orElseThrow(() -> new LocationNotFoundException(eraCode));
        List<LocationView> views = locations.findByEraIdOrderByName(era.getId()).stream()
                .map(l -> LocationView.of(l, true))
                .toList();
        return new LocationsResponse(era.getCode(), null, views);
    }

    /** Travel within the era to a connected location. Returns the refreshed era view. */
    @Transactional
    public LocationsResponse travelTo(UUID accountId, UUID characterId, String locationCode) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        Location target = locations.findByEraIdAndCode(character.getCurrentEra().getId(), locationCode)
                .orElseThrow(() -> new LocationNotFoundException(locationCode));

        UUID currentId = character.getCurrentLocationId();
        if (currentId != null && !currentId.equals(target.getId())
                && !links.existsByFromLocationIdAndToLocationId(currentId, target.getId())) {
            throw new LocationNotConnectedException(locationCode);
        }
        characters.placeAt(character, target.getId());
        return forCharacter(accountId, characterId);
    }
}
