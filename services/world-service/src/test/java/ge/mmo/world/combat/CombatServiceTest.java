package ge.mmo.world.combat;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.economy.EconomyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CombatServiceTest {

    private EnemyDefRepository enemies;
    private EncounterRepository encounters;
    private CharacterService characters;
    private EconomyService economy;
    private ge.mmo.world.siege.SiegeService sieges;
    private ge.mmo.world.clan.ClanService clans;
    private CombatService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();
    private final UUID enemyId = UUID.randomUUID();
    private final UUID encId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        enemies = mock(EnemyDefRepository.class);
        encounters = mock(EncounterRepository.class);
        characters = mock(CharacterService.class);
        economy = mock(EconomyService.class);
        sieges = mock(ge.mmo.world.siege.SiegeService.class);
        clans = mock(ge.mmo.world.clan.ClanService.class);
        service = new CombatService(enemies, encounters, characters, economy, sieges, clans);
        when(characters.requireOwned(any(), any())).thenReturn(mock(PlayerCharacter.class));
    }

    private EnemyDef enemy(int attackPower, long goldReward) {
        EnemyDef e = mock(EnemyDef.class);
        when(e.getId()).thenReturn(enemyId);
        when(e.getCode()).thenReturn("ECHO_OF_THE_CROWN");
        when(e.getName()).thenReturn("Echo of the Crown");
        when(e.getAttackPower()).thenReturn(attackPower);
        when(e.getGoldReward()).thenReturn(goldReward);
        return e;
    }

    @Test
    void attackDamagesEnemyThenRetaliates() {
        Encounter encounter = new Encounter(encId, enemyId, charId, 100, 100);
        EnemyDef enemy = enemy(10, 50);
        when(encounters.findById(encId)).thenReturn(Optional.of(encounter));
        when(enemies.findById(enemyId)).thenReturn(Optional.of(enemy));

        var view = service.attack(acc, charId, encId);

        assertThat(view.enemyHp()).isEqualTo(75);      // 100 - PLAYER_ATTACK(25)
        assertThat(view.characterHp()).isEqualTo(90);  // 100 - enemy attack(10)
        assertThat(view.status()).isEqualTo("ACTIVE");
        assertThat(view.goldAwarded()).isZero();
    }

    @Test
    void killingEnemyWinsAndAwardsGold() {
        Encounter encounter = new Encounter(encId, enemyId, charId, 20, 100); // enemy almost dead
        EnemyDef enemy = enemy(10, 50);
        when(encounters.findById(encId)).thenReturn(Optional.of(encounter));
        when(enemies.findById(enemyId)).thenReturn(Optional.of(enemy));

        var view = service.attack(acc, charId, encId);

        assertThat(view.enemyHp()).isZero();
        assertThat(view.status()).isEqualTo("WON");
        assertThat(view.characterHp()).isEqualTo(100); // no retaliation once dead
        assertThat(view.goldAwarded()).isEqualTo(50);
        verify(economy).reward(charId, 50);
    }

    @Test
    void cannotStartWhileAlreadyFighting() {
        when(encounters.findByCharacterIdAndStatus(charId, Encounter.Status.ACTIVE))
                .thenReturn(Optional.of(new Encounter(encId, enemyId, charId, 100, 100)));
        assertThatThrownBy(() -> service.start(acc, charId, "ECHO_OF_THE_CROWN"))
                .isInstanceOf(AlreadyInEncounterException.class);
    }

    @Test
    void startForSiegeYourClanIsNotContestingIsRejected() {
        UUID siegeId = UUID.randomUUID();
        UUID clanId = UUID.randomUUID();
        when(encounters.findByCharacterIdAndStatus(charId, Encounter.Status.ACTIVE)).thenReturn(Optional.empty());
        when(clans.clanIdOf(charId)).thenReturn(Optional.of(clanId));
        when(sieges.isActiveParticipant(siegeId, clanId)).thenReturn(false);

        assertThatThrownBy(() -> service.start(acc, charId, "ECHO_OF_THE_CROWN", siegeId))
                .isInstanceOf(NotInSiegeContextException.class);
    }

    @Test
    void winningASiegeEncounterFeedsTheClanScore() {
        UUID siegeId = UUID.randomUUID();
        UUID clanId = UUID.randomUUID();
        Encounter encounter = new Encounter(encId, enemyId, charId, 20, 100); // one hit kills
        encounter.setSiegeId(siegeId);
        EnemyDef enemy = enemy(10, 50);
        when(enemy.getMaxHp()).thenReturn(160);
        when(encounters.findById(encId)).thenReturn(Optional.of(encounter));
        when(enemies.findById(enemyId)).thenReturn(Optional.of(enemy));
        when(clans.clanIdOf(charId)).thenReturn(Optional.of(clanId));

        service.attack(acc, charId, encId);

        verify(sieges).recordCombatContribution(siegeId, clanId, 160L);
    }
}
