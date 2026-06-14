package ge.mmo.world.clan;

/** Domain failures of the clan module. */
public final class ClanExceptions {
    private ClanExceptions() {
    }
}

class AlreadyInClanException extends RuntimeException {
    AlreadyInClanException() {
        super("Character is already in a clan");
    }
}

class NotInClanException extends RuntimeException {
    NotInClanException() {
        super("Character is not in a clan");
    }
}

class ClanNameTakenException extends RuntimeException {
    ClanNameTakenException(String name) {
        super("Clan name already taken: " + name);
    }
}

class ClanTagTakenException extends RuntimeException {
    ClanTagTakenException(String tag) {
        super("Clan tag already taken: " + tag);
    }
}

class ClanNotFoundException extends RuntimeException {
    ClanNotFoundException() {
        super("Clan not found");
    }
}

class ClanFullException extends RuntimeException {
    ClanFullException() {
        super("Clan is full");
    }
}

class NoClanInviteException extends RuntimeException {
    NoClanInviteException() {
        super("No pending invite for this clan");
    }
}

/** The acting character lacks the rank to perform this clan action. */
class InsufficientClanRankException extends RuntimeException {
    InsufficientClanRankException() {
        super("Insufficient clan rank for this action");
    }
}
