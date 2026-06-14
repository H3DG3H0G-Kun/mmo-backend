package ge.mmo.world.character;

import java.util.UUID;

/** Domain failures for the character module. */
public final class CharacterExceptions {
    private CharacterExceptions() {
    }
}

class CharacterNameTakenException extends RuntimeException {
    CharacterNameTakenException(String name) {
        super("Character name already taken: " + name);
    }
}

class TooManyCharactersException extends RuntimeException {
    TooManyCharactersException(int max) {
        super("Account already has the maximum of " + max + " characters");
    }
}

class NoStartingEraException extends RuntimeException {
    NoStartingEraException() {
        super("No starting era is configured (no era with default_unlocked = true)");
    }
}

class CharacterNotFoundException extends RuntimeException {
    CharacterNotFoundException(UUID id) {
        super("Character not found: " + id);
    }
}
