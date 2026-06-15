package ge.mmo.world.world;

import java.util.List;

/** Read models for the world-geography endpoints. */
public final class LocationViews {
    private LocationViews() {
    }

    /** A location as seen by a character: includes whether it's reachable from where they stand. */
    public record LocationView(
            String code,
            String name,
            LocationType type,
            String region,
            double x,
            double y,
            String description,
            boolean connected) {

        static LocationView of(Location l, boolean connected) {
            return new LocationView(l.getCode(), l.getName(), l.getType(), l.getRegion(),
                    l.getX(), l.getY(), l.getDescription(), connected);
        }
    }

    /** The locations of an era plus where the character currently stands. */
    public record LocationsResponse(
            String eraCode,
            String currentLocationCode, // null = just arrived in the era, not yet placed
            List<LocationView> locations) {
    }
}
