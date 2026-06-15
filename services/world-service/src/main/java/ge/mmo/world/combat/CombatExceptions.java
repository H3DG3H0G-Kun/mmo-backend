package ge.mmo.world.combat;

/** Domain failures of the combat module. */
public final class CombatExceptions {
    private CombatExceptions() {
    }
}

class EnemyNotFoundException extends RuntimeException {
    EnemyNotFoundException(String code) {
        super("Enemy not found: " + code);
    }
}

class EncounterNotFoundException extends RuntimeException {
    EncounterNotFoundException() {
        super("Encounter not found");
    }
}

class EncounterNotActiveException extends RuntimeException {
    EncounterNotActiveException() {
        super("Encounter is already over");
    }
}

class AlreadyInEncounterException extends RuntimeException {
    AlreadyInEncounterException() {
        super("Character is already in a fight");
    }
}

/** Tried to fight for a siege the character's clan is not contesting (or that is not active). */
class NotInSiegeContextException extends RuntimeException {
    NotInSiegeContextException() {
        super("Your clan is not contesting that active siege");
    }
}
