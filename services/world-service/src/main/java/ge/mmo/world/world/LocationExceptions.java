package ge.mmo.world.world;

/** Domain failures of the world-geography module. */
public final class LocationExceptions {
    private LocationExceptions() {
    }
}

/** No such location in the character's current era. */
class LocationNotFoundException extends RuntimeException {
    LocationNotFoundException(String code) {
        super("No such location in this era: " + code);
    }
}

/** The destination is not connected to where the character currently stands. */
class LocationNotConnectedException extends RuntimeException {
    LocationNotConnectedException(String code) {
        super("You cannot travel there from here: " + code);
    }
}
