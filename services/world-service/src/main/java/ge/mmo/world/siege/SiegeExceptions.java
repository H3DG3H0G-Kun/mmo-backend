package ge.mmo.world.siege;

/** Domain failures of the siege module. */
public final class SiegeExceptions {
    private SiegeExceptions() {
    }
}

class PlaceNotFoundException extends RuntimeException {
    PlaceNotFoundException(String code) {
        super("Place of power not found: " + code);
    }
}

class SiegeNotFoundException extends RuntimeException {
    SiegeNotFoundException() {
        super("Siege not found");
    }
}

/** Only a clan leader may declare/start/resolve a siege. */
class NotClanLeaderException extends RuntimeException {
    NotClanLeaderException() {
        super("Only a clan leader may do this");
    }
}

/** The acting character must belong to a clan. */
class ClanRequiredException extends RuntimeException {
    ClanRequiredException() {
        super("Character must be in a clan");
    }
}

class SiegeAlreadyOpenException extends RuntimeException {
    SiegeAlreadyOpenException() {
        super("This place already has an active or declared siege");
    }
}

/** The siege is not in the state this action requires. */
class SiegeStateException extends RuntimeException {
    SiegeStateException(String detail) {
        super(detail);
    }
}

class NotASiegeParticipantException extends RuntimeException {
    NotASiegeParticipantException() {
        super("Your clan is not contesting this siege");
    }
}
