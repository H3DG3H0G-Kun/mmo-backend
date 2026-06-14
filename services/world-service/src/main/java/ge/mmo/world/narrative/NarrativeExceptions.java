package ge.mmo.world.narrative;

/** Domain failures of the narrative engine. Mapped to HTTP status in the web layer. */
public final class NarrativeExceptions {
    private NarrativeExceptions() {
    }
}

class TaleNotFoundException extends RuntimeException {
    TaleNotFoundException(String ref) {
        super("Tale not found: " + ref);
    }
}

/** The Tale's Resonance is not open for the Watcher's current context (conditions unmet). */
class ResonanceClosedException extends RuntimeException {
    ResonanceClosedException(String code) {
        super("This tale does not resonate here yet: " + code);
    }
}

/** Tried to advance a Tale the Watcher has not entered (or already finished). */
class NoActiveProgressException extends RuntimeException {
    NoActiveProgressException(String code) {
        super("No active progress in tale: " + code);
    }
}

/** A CHOOSE beat needs a valid choiceKey matching one of its branches. */
class InvalidChoiceException extends RuntimeException {
    InvalidChoiceException(String detail) {
        super(detail);
    }
}

/** Starting/advancing a co-op run requires the character to be in a party. */
class PartyRequiredException extends RuntimeException {
    PartyRequiredException() {
        super("Character must be in a party for a co-op tale");
    }
}

/** Only the party leader may start a co-op run. */
class NotInstanceLeaderException extends RuntimeException {
    NotInstanceLeaderException() {
        super("Only the party leader may begin a co-op tale");
    }
}

/** The party already has an active co-op run. */
class InstanceAlreadyActiveException extends RuntimeException {
    InstanceAlreadyActiveException() {
        super("The party is already in a tale");
    }
}

/** No active co-op run for the party. */
class NoActiveInstanceException extends RuntimeException {
    NoActiveInstanceException() {
        super("The party is not in a tale");
    }
}
