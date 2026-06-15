package ge.mmo.world.character;

import ge.mmo.world.world.Era;
import ge.mmo.world.world.EraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CharacterService {

    static final int MAX_CHARACTERS_PER_ACCOUNT = 5;

    private final PlayerCharacterRepository characters;
    private final EraRepository eras;

    public CharacterService(PlayerCharacterRepository characters, EraRepository eras) {
        this.characters = characters;
        this.eras = eras;
    }

    /** Create a character for the account. New Watchers begin in the earliest open era. */
    @Transactional
    public PlayerCharacter create(UUID accountId, String name) {
        if (characters.countByAccountId(accountId) >= MAX_CHARACTERS_PER_ACCOUNT) {
            throw new TooManyCharactersException(MAX_CHARACTERS_PER_ACCOUNT);
        }
        if (characters.existsByNameIgnoreCase(name)) {
            throw new CharacterNameTakenException(name);
        }
        Era startingEra = eras.findByDefaultUnlockedTrueOrderByOrdinalAsc().stream()
                .findFirst()
                .orElseThrow(NoStartingEraException::new);

        PlayerCharacter character = new PlayerCharacter(UUID.randomUUID(), accountId, name, startingEra);
        return characters.save(character);
    }

    @Transactional(readOnly = true)
    public List<PlayerCharacter> listForAccount(UUID accountId) {
        return characters.findByAccountIdOrderByCreatedAtAsc(accountId);
    }

    /** Fetch characters by id (used to render party member lists). */
    @Transactional(readOnly = true)
    public List<PlayerCharacter> byIds(Collection<UUID> ids) {
        return characters.findAllById(ids);
    }

    /** Fetch a character the account owns, or throw. Used by world/instance entry validation. */
    @Transactional(readOnly = true)
    public PlayerCharacter requireOwned(UUID accountId, UUID characterId) {
        return findOwned(accountId, characterId)
                .orElseThrow(() -> new CharacterNotFoundException(characterId));
    }

    /** Fetch a character the account owns, if present. */
    @Transactional(readOnly = true)
    public Optional<PlayerCharacter> findOwned(UUID accountId, UUID characterId) {
        return characters.findByIdAndAccountId(characterId, accountId);
    }

    /** Persist a character's move to a new era (the timeline layer validates eligibility first). */
    @Transactional
    public PlayerCharacter relocate(PlayerCharacter character, Era era) {
        character.moveToEra(era);
        return characters.save(character);
    }

    /** Persist an intra-era move to a location (the world layer validates connectivity first). */
    @Transactional
    public PlayerCharacter placeAt(PlayerCharacter character, java.util.UUID locationId) {
        character.moveToLocation(locationId);
        return characters.save(character);
    }
}
