package ge.mmo.world.siege;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.clan.ClanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SiegeServiceTest {

    private PlaceOfPowerRepository places;
    private SiegeRepository sieges;
    private SiegeParticipantRepository participants;
    private CharacterService characters;
    private ClanService clans;
    private SiegeService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();
    private final UUID clanA = UUID.randomUUID();
    private final UUID clanB = UUID.randomUUID();
    private final UUID siegeId = UUID.randomUUID();
    private final UUID placeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        places = mock(PlaceOfPowerRepository.class);
        sieges = mock(SiegeRepository.class);
        participants = mock(SiegeParticipantRepository.class);
        characters = mock(CharacterService.class);
        clans = mock(ClanService.class);
        service = new SiegeService(places, sieges, participants, characters, clans);
        when(characters.requireOwned(any(), any())).thenReturn(mock(PlayerCharacter.class));
    }

    private void asLeaderOfA() {
        when(clans.clanIdOf(charId)).thenReturn(Optional.of(clanA));
        when(clans.isClanLeader(charId)).thenReturn(true);
    }

    @Test
    void declareRequiresClanLeadership() {
        when(clans.clanIdOf(charId)).thenReturn(Optional.of(clanA));
        when(clans.isClanLeader(charId)).thenReturn(false);
        assertThatThrownBy(() -> service.declare(acc, charId, "ARMAZTSIKHE"))
                .isInstanceOf(NotClanLeaderException.class);
    }

    @Test
    void declareRequiresAClan() {
        when(clans.clanIdOf(charId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.declare(acc, charId, "ARMAZTSIKHE"))
                .isInstanceOf(ClanRequiredException.class);
    }

    @Test
    void contributeByNonParticipantClanIsRejected() {
        Siege siege = new Siege(siegeId, placeId, clanA);
        siege.activate();
        when(sieges.findById(siegeId)).thenReturn(Optional.of(siege));
        when(clans.clanIdOf(charId)).thenReturn(Optional.of(clanB));
        when(participants.findBySiegeIdAndClanId(siegeId, clanB)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.contribute(acc, charId, siegeId, 10))
                .isInstanceOf(NotASiegeParticipantException.class);
    }

    @Test
    void resolveCapturesPlaceForTopScoringClan() {
        Siege siege = new Siege(siegeId, placeId, clanA); // clanA declared
        siege.activate();
        asLeaderOfA();
        when(sieges.findById(siegeId)).thenReturn(Optional.of(siege));

        SiegeParticipant pB = new SiegeParticipant(UUID.randomUUID(), siegeId, clanB);
        pB.addScore(50);
        SiegeParticipant pA = new SiegeParticipant(UUID.randomUUID(), siegeId, clanA);
        pA.addScore(10);
        when(participants.findBySiegeIdOrderByScoreDesc(siegeId)).thenReturn(List.of(pB, pA)); // desc

        PlaceOfPower place = mock(PlaceOfPower.class);
        when(places.findById(placeId)).thenReturn(Optional.of(place));

        var view = service.resolve(acc, charId, siegeId);

        assertThat(view.status()).isEqualTo("RESOLVED");
        assertThat(view.winnerClanId()).isEqualTo(clanB);
        verify(place).captureBy(clanB);
    }
}
