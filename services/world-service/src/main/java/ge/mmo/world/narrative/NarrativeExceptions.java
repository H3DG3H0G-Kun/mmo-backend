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
