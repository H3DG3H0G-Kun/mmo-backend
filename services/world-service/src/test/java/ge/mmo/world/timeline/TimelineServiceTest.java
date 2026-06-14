package ge.mmo.world.timeline;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.world.Era;
import ge.mmo.world.world.EraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TimelineServiceTest {

    private EraUnlockRepository unlocks;
    private TimelineProgressRepository progress;
    private EraRepository eras;
    private CharacterService characters;
    private TimelineService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        unlocks = mock(EraUnlockRepository.class);
        progress = mock(TimelineProgressRepository.class);
        eras = mock(EraRepository.class);
        characters = mock(CharacterService.class);
        service = new TimelineService(unlocks, progress, eras, characters);
    }

    private Era era(int id, String code, boolean defaultUnlocked) {
        Era e = mock(Era.class);
        when(e.getId()).thenReturn(id);
        when(e.getCode()).thenReturn(code);
        when(e.isDefaultUnlocked()).thenReturn(defaultUnlocked);
        return e;
    }

    @Test
    void defaultUnlockedEraIsAlwaysAccessible() {
        assertThat(service.isUnlocked(charId, era(1, "PARNAVAZ", true))).isTrue();
    }

    @Test
    void lockedEraIsInaccessibleWithoutUnlockRow() {
        Era e = era(3, "GOLDEN_AGE", false);
        when(unlocks.existsByCharacterIdAndEraId(charId, 3)).thenReturn(false);
        assertThat(service.isUnlocked(charId, e)).isFalse();
    }

    @Test
    void unlockRowGrantsAccess() {
        Era e = era(2, "EARLY_KING", false);
        when(unlocks.existsByCharacterIdAndEraId(charId, 2)).thenReturn(true);
        assertThat(service.isUnlocked(charId, e)).isTrue();
    }

    @Test
    void travelToLockedEraIsRejected() {
        Era e = era(3, "GOLDEN_AGE", false);
        when(characters.requireOwned(acc, charId)).thenReturn(mock(PlayerCharacter.class));
        when(eras.findByCode("GOLDEN_AGE")).thenReturn(Optional.of(e));
        when(unlocks.existsByCharacterIdAndEraId(charId, 3)).thenReturn(false);

        assertThatThrownBy(() -> service.travel(acc, charId, "GOLDEN_AGE"))
                .isInstanceOf(EraLockedException.class);
        verify(characters, never()).relocate(any(), any());
    }

    @Test
    void travelToUnlockedEraRelocates() {
        Era e = era(2, "EARLY_KING", false);
        PlayerCharacter character = mock(PlayerCharacter.class);
        PlayerCharacter moved = mock(PlayerCharacter.class);
        when(characters.requireOwned(acc, charId)).thenReturn(character);
        when(eras.findByCode("EARLY_KING")).thenReturn(Optional.of(e));
        when(unlocks.existsByCharacterIdAndEraId(charId, 2)).thenReturn(true);
        when(characters.relocate(character, e)).thenReturn(moved);

        assertThat(service.travel(acc, charId, "EARLY_KING")).isSameAs(moved);
    }

    @Test
    void unlockEraIsIdempotent() {
        when(unlocks.existsByCharacterIdAndEraId(charId, 2)).thenReturn(true);
        service.unlockEra(charId, 2);
        verify(unlocks, never()).save(any());

        when(unlocks.existsByCharacterIdAndEraId(charId, 3)).thenReturn(false);
        service.unlockEra(charId, 3);
        verify(unlocks).save(any(EraUnlock.class));
    }
}
