package ge.mmo.world.timeline;

import ge.mmo.world.world.Era;

/** Read models for the timeline/world endpoints. */
public final class TimelineViews {
    private TimelineViews() {
    }

    /** An era as seen by a character: is it open to travel? */
    public record EraStatusView(Integer id, String code, String name, int ordinal, boolean unlocked) {
        public static EraStatusView of(Era era, boolean unlocked) {
            return new EraStatusView(era.getId(), era.getCode(), era.getName(), era.getOrdinal(), unlocked);
        }
    }

    /** A line of the server-wide Living Timeline. */
    public record TimelineEraView(Integer eraId, String code, String name, int ordinal, long restoredCount) {
    }
}
