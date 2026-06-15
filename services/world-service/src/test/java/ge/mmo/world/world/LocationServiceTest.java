package ge.mmo.world.world;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocationServiceTest {

    private LocationRepository locations;
    private LocationLinkRepository links;
    private EraRepository eras;
    private CharacterService characters;
    private LocationService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();
    private final UUID locA = UUID.randomUUID();
    private final UUID locB = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        locations = mock(LocationRepository.class);
        links = mock(LocationLinkRepository.class);
        eras = mock(EraRepository.class);
        characters = mock(CharacterService.class);
        service = new LocationService(locations, links, eras, characters);

        PlayerCharacter character = mock(PlayerCharacter.class);
        Era era = mock(Era.class);
        when(era.getId()).thenReturn(1);
        when(character.getCurrentEra()).thenReturn(era);
        when(character.getCurrentLocationId()).thenReturn(locA);
        when(characters.requireOwned(acc, charId)).thenReturn(character);
    }

    @Test
    void travelToUnknownLocationIsRejected() {
        when(locations.findByEraIdAndCode(1, "nowhere")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.travelTo(acc, charId, "nowhere"))
                .isInstanceOf(LocationNotFoundException.class);
    }

    @Test
    void travelToDisconnectedLocationIsRejected() {
        Location target = mock(Location.class);
        when(target.getId()).thenReturn(locB);
        when(locations.findByEraIdAndCode(1, "far")).thenReturn(Optional.of(target));
        when(links.existsByFromLocationIdAndToLocationId(locA, locB)).thenReturn(false);

        assertThatThrownBy(() -> service.travelTo(acc, charId, "far"))
                .isInstanceOf(LocationNotConnectedException.class);
        verify(characters, never()).placeAt(any(), any());
    }
}
