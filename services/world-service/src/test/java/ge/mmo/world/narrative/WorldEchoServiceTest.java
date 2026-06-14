package ge.mmo.world.narrative;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.timeline.TimelineService;
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

class WorldEchoServiceTest {

    private TaleRepository tales;
    private SagaRepository sagas;
    private TriggerConditionRepository conditions;
    private TaleProgressRepository progressRepo;
    private WorldEchoRepository echoes;
    private WorldEchoParticipationRepository participations;
    private ResonanceEvaluator evaluator;
    private ResonanceContextFactory contextFactory;
    private CharacterService characters;
    private TimelineService timeline;
    private WorldEchoService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();
    private final UUID taleId = UUID.randomUUID();
    private final UUID sagaId = UUID.randomUUID();
    private final UUID echoId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        tales = mock(TaleRepository.class);
        sagas = mock(SagaRepository.class);
        conditions = mock(TriggerConditionRepository.class);
        progressRepo = mock(TaleProgressRepository.class);
        echoes = mock(WorldEchoRepository.class);
        participations = mock(WorldEchoParticipationRepository.class);
        evaluator = mock(ResonanceEvaluator.class);
        contextFactory = mock(ResonanceContextFactory.class);
        characters = mock(CharacterService.class);
        timeline = mock(TimelineService.class);
        service = new WorldEchoService(tales, sagas, conditions, progressRepo, echoes, participations,
                evaluator, contextFactory, characters, timeline);
        when(characters.requireOwned(any(), any())).thenReturn(mock(PlayerCharacter.class));
    }

    @Test
    void summonWhenAlreadyActiveIsRejected() {
        Tale tale = mock(Tale.class);
        when(tale.getId()).thenReturn(taleId);
        when(tales.findByCode("TALE")).thenReturn(Optional.of(tale));
        when(echoes.findByTaleIdAndStatus(taleId, WorldEcho.Status.ACTIVE))
                .thenReturn(Optional.of(new WorldEcho(UUID.randomUUID(), taleId, 100)));

        assertThatThrownBy(() -> service.summon(acc, charId, "TALE", null, null))
                .isInstanceOf(WorldEchoActiveException.class);
    }

    @Test
    void contributeReachingGoalResolvesAndRewardsEveryone() {
        WorldEcho echo = new WorldEcho(echoId, taleId, 100);
        Tale tale = mock(Tale.class);
        when(tale.getId()).thenReturn(taleId);
        when(tale.getCode()).thenReturn("TALE");
        when(tale.getTitle()).thenReturn("A Tale");
        when(tale.getUnlocksEraId()).thenReturn(2);
        when(tale.getSagaId()).thenReturn(sagaId);
        Saga saga = mock(Saga.class);
        when(saga.getEraId()).thenReturn(1);

        when(echoes.findById(echoId)).thenReturn(Optional.of(echo));
        when(participations.findByWorldEchoIdAndCharacterId(echoId, charId)).thenReturn(Optional.empty());
        when(tales.findById(taleId)).thenReturn(Optional.of(tale));
        when(participations.findByWorldEchoId(echoId))
                .thenReturn(List.of(new WorldEchoParticipation(UUID.randomUUID(), echoId, charId)));
        when(progressRepo.findByCharacterIdAndTaleId(charId, taleId)).thenReturn(Optional.empty());
        when(sagas.findById(sagaId)).thenReturn(Optional.of(saga));
        when(participations.countByWorldEchoId(echoId)).thenReturn(1L);

        var view = service.contribute(acc, charId, echoId, 100);

        assertThat(echo.getStatus()).isEqualTo(WorldEcho.Status.RESOLVED);
        assertThat(view.status()).isEqualTo("RESOLVED");
        verify(timeline).unlockEra(charId, 2);
        verify(timeline).recordRestoration(1);
    }
}
