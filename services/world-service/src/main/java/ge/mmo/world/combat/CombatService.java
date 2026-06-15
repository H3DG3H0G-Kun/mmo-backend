package ge.mmo.world.combat;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.clan.ClanService;
import ge.mmo.world.combat.CombatViews.EncounterView;
import ge.mmo.world.combat.CombatViews.EnemyView;
import ge.mmo.world.economy.EconomyService;
import ge.mmo.world.siege.SiegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Backend-authoritative combat. v1 is deterministic and turn-based: each ATTACK, the Watcher
 * strikes the Distortion, and (if it survives) the Distortion strikes back — the server resolves
 * all damage and decides win/lose. Victory awards the enemy's gold and, if the fight was joined
 * for a siege your clan contests, adds the Distortion's strength to your clan's siege score.
 */
@Service
public class CombatService {

    static final int CHARACTER_MAX_HP = 100;
    static final int PLAYER_ATTACK = 25;

    private final EnemyDefRepository enemies;
    private final EncounterRepository encounters;
    private final CharacterService characters;
    private final EconomyService economy;
    private final SiegeService sieges;
    private final ClanService clans;

    public CombatService(EnemyDefRepository enemies, EncounterRepository encounters,
                         CharacterService characters, EconomyService economy,
                         SiegeService sieges, ClanService clans) {
        this.enemies = enemies;
        this.encounters = encounters;
        this.characters = characters;
        this.economy = economy;
        this.sieges = sieges;
        this.clans = clans;
    }

    @Transactional(readOnly = true)
    public List<EnemyView> enemies() {
        return enemies.findAllByOrderByMaxHpAsc().stream().map(EnemyView::of).toList();
    }

    /** Begin a fight against a Distortion. One active encounter per character. */
    @Transactional
    public EncounterView start(UUID accountId, UUID characterId, String enemyCode) {
        return start(accountId, characterId, enemyCode, null);
    }

    /**
     * Begin a fight, optionally "for" a siege. If {@code siegeId} is given, the character's clan
     * must be contesting that ACTIVE siege; victory then feeds the clan's siege score.
     */
    @Transactional
    public EncounterView start(UUID accountId, UUID characterId, String enemyCode, UUID siegeId) {
        characters.requireOwned(accountId, characterId);
        if (encounters.findByCharacterIdAndStatus(characterId, Encounter.Status.ACTIVE).isPresent()) {
            throw new AlreadyInEncounterException();
        }
        if (siegeId != null) {
            UUID clanId = clans.clanIdOf(characterId).orElseThrow(NotInSiegeContextException::new);
            if (!sieges.isActiveParticipant(siegeId, clanId)) {
                throw new NotInSiegeContextException();
            }
        }
        EnemyDef enemy = enemies.findByCode(enemyCode).orElseThrow(() -> new EnemyNotFoundException(enemyCode));
        Encounter encounter = new Encounter(
                UUID.randomUUID(), enemy.getId(), characterId, enemy.getMaxHp(), CHARACTER_MAX_HP);
        encounter.setSiegeId(siegeId);
        encounters.save(encounter);
        return view(encounter, enemy, 0);
    }

    /** Resolve one round: the Watcher strikes; if the Distortion lives, it strikes back. */
    @Transactional
    public EncounterView attack(UUID accountId, UUID characterId, UUID encounterId) {
        characters.requireOwned(accountId, characterId);
        Encounter encounter = encounters.findById(encounterId).orElseThrow(EncounterNotFoundException::new);
        if (!encounter.getCharacterId().equals(characterId) || encounter.getStatus() != Encounter.Status.ACTIVE) {
            throw new EncounterNotActiveException();
        }
        EnemyDef enemy = enemies.findById(encounter.getEnemyDefId()).orElseThrow(EncounterNotFoundException::new);

        encounter.damageEnemy(PLAYER_ATTACK);
        long awarded = 0;
        if (encounter.getEnemyHp() <= 0) {
            encounter.win();
            economy.reward(characterId, enemy.getGoldReward());
            awarded = enemy.getGoldReward();
            // Combat -> siege: a Distortion felled for a siege adds its strength to the clan's score.
            if (encounter.getSiegeId() != null) {
                clans.clanIdOf(characterId).ifPresent(clanId ->
                        sieges.recordCombatContribution(encounter.getSiegeId(), clanId, enemy.getMaxHp()));
            }
        } else {
            encounter.damageCharacter(enemy.getAttackPower());
            if (encounter.getCharacterHp() <= 0) {
                encounter.lose();
            }
        }
        encounters.save(encounter);
        return view(encounter, enemy, awarded);
    }

    @Transactional(readOnly = true)
    public EncounterView active(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        Encounter encounter = encounters.findByCharacterIdAndStatus(characterId, Encounter.Status.ACTIVE)
                .orElseThrow(EncounterNotFoundException::new);
        return view(encounter, enemies.findById(encounter.getEnemyDefId()).orElseThrow(EncounterNotFoundException::new), 0);
    }

    private EncounterView view(Encounter e, EnemyDef enemy, long awarded) {
        return new EncounterView(e.getId(), enemy.getCode(), enemy.getName(),
                e.getEnemyHp(), e.getCharacterHp(), e.getStatus().name(), awarded);
    }
}
