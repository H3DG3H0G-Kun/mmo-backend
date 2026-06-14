package ge.mmo.world.timeline;

/** Domain failures of the timeline/world-travel module. */
public final class TimelineExceptions {
    private TimelineExceptions() {
    }
}

class EraNotFoundException extends RuntimeException {
    EraNotFoundException(String code) {
        super("Era not found: " + code);
    }
}

/** The Watcher has not yet unlocked this era — travel denied. */
class EraLockedException extends RuntimeException {
    EraLockedException(String code) {
        super("This era is not open to you yet: " + code);
    }
}
