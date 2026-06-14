package ge.mmo.world.narrative;

import ge.mmo.world.narrative.ContentPackage.BeatDef;
import ge.mmo.world.narrative.ContentPackage.EdgeDef;
import ge.mmo.world.narrative.ContentPackage.SagaDef;
import ge.mmo.world.narrative.ContentPackage.TaleDef;
import ge.mmo.world.narrative.ContentPackage.TriggerDef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentImportServiceTest {

    private SagaRepository sagas;
    private TaleRepository tales;
    private BeatRepository beats;
    private BeatEdgeRepository edges;
    private TriggerConditionRepository conditions;
    private ContentImportService service;

    @BeforeEach
    void setUp() {
        sagas = mock(SagaRepository.class);
        tales = mock(TaleRepository.class);
        beats = mock(BeatRepository.class);
        edges = mock(BeatEdgeRepository.class);
        conditions = mock(TriggerConditionRepository.class);
        service = new ContentImportService(sagas, tales, beats, edges, conditions);
        when(sagas.findByCode(any())).thenReturn(Optional.empty());
        when(tales.findByCode(any())).thenReturn(Optional.empty());
        when(sagas.save(any())).thenAnswer(i -> i.getArgument(0));
        when(tales.save(any())).thenAnswer(i -> i.getArgument(0));
        when(beats.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    private ContentPackage samplePackage() {
        return new ContentPackage(
                new SagaDef("SAGA_GUEST_HOST", "Guest and Host", "WORD", 1, 2),
                List.of(new TaleDef("TALE_MOUNTAIN_MEETING", "The Mountain Meeting", "TRUE_TALE", 1, null,
                        List.of(new BeatDef("ARRIVE", 1, "On a storm-lashed peak two strangers meet.", "WITNESS", false),
                                new BeatDef("HONOR", 2, "Hold the bond of guest and host true.", "WARD", true)),
                        List.of(new EdgeDef("ARRIVE", "HONOR", null)),
                        List.of(new TriggerDef("PLACE", "mountain"), new TriggerDef("STATE", "STORM")))));
    }

    @Test
    void importsSagaTaleBeatsEdgesAndTriggers() {
        var summary = service.importContent(samplePackage());

        assertThat(summary.sagaCode()).isEqualTo("SAGA_GUEST_HOST");
        assertThat(summary.tales()).isEqualTo(1);
        assertThat(summary.beats()).isEqualTo(2);
        verify(beats, times(2)).save(any());
        verify(edges, times(1)).save(any());
        verify(conditions, times(2)).save(any());
    }

    @Test
    void duplicateSagaIsRejected() {
        when(sagas.findByCode("SAGA_GUEST_HOST")).thenReturn(Optional.of(mock(Saga.class)));
        assertThatThrownBy(() -> service.importContent(samplePackage()))
                .isInstanceOf(ContentConflictException.class);
    }

    @Test
    void invalidStrandIsRejected() {
        ContentPackage bad = new ContentPackage(
                new SagaDef("SAGA_X", "X", "NONSENSE", 1, 1), List.of());
        assertThatThrownBy(() -> service.importContent(bad))
                .isInstanceOf(ContentValidationException.class);
    }
}
