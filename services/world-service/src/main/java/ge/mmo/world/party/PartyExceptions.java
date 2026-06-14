package ge.mmo.world.party;

/** Domain failures of the party module. */
public final class PartyExceptions {
    private PartyExceptions() {
    }
}

class AlreadyInPartyException extends RuntimeException {
    AlreadyInPartyException() {
        super("Character is already in a party");
    }
}

class NotInPartyException extends RuntimeException {
    NotInPartyException() {
        super("Character is not in a party");
    }
}

class PartyNotFoundException extends RuntimeException {
    PartyNotFoundException() {
        super("Party not found");
    }
}

class PartyFullException extends RuntimeException {
    PartyFullException() {
        super("Party is full");
    }
}

class PartyClosedException extends RuntimeException {
    PartyClosedException() {
        super("Party is not open to join");
    }
}

class NotPartyLeaderException extends RuntimeException {
    NotPartyLeaderException() {
        super("Only the party leader may do this");
    }
}
