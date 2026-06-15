package ge.mmo.world.world;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** Server-spawned NPCs, scoped to an era (the area-of-interest unit) for the presence stream. */
@Service
public class NpcService {

    /** A live NPC as seen in the world: code is its stable id in the presence stream. */
    public record NpcView(String code, String name, String role, String locationCode, double x, double y) {
        static NpcView of(Npc n) {
            return new NpcView(n.getCode(), n.getName(), n.getRole(), n.getLocationCode(), n.getX(), n.getY());
        }
    }

    private final NpcRepository npcs;
    private final CharacterService characters;

    public NpcService(NpcRepository npcs, CharacterService characters) {
        this.npcs = npcs;
        this.characters = characters;
    }

    /** NPCs present in an era (used to seed a joining Watcher's world snapshot). */
    @Transactional(readOnly = true)
    public List<NpcView> inEra(Integer eraId) {
        return npcs.findByEraId(eraId).stream().map(NpcView::of).toList();
    }

    /** NPCs in the character's current era (REST convenience). */
    @Transactional(readOnly = true)
    public List<NpcView> forCharacter(UUID accountId, UUID characterId) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        return inEra(character.getCurrentEra().getId());
    }
}
