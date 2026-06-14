package ge.mmo.world.session;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Owns "what is this connection doing in the world" — the realtime counterpart to the REST
 * account/character layer. For now it validates world entry (you may only enter the world as a
 * character you own). Later: presence, zone/interest tracking, party binding.
 */
@Service
public class WorldSessionService {

    private final CharacterService characters;

    public WorldSessionService(CharacterService characters) {
        this.characters = characters;
    }

    /** Validate that the account owns the character; the backend decides, not the client. */
    public Optional<PlayerCharacter> enterWorld(UUID accountId, UUID characterId) {
        return characters.findOwned(accountId, characterId);
    }
}
