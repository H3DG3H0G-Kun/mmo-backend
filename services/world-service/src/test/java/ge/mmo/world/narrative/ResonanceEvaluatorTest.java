package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.ConditionType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResonanceEvaluatorTest {

    private final ResonanceEvaluator evaluator = new ResonanceEvaluator();

    private TriggerCondition cond(ConditionType type, String value) {
        TriggerCondition c = mock(TriggerCondition.class);
        when(c.getType()).thenReturn(type);
        when(c.getValue()).thenReturn(value);
        return c;
    }

    private ResonanceContext at(String era, String place, Set<String> states, Set<String> completed) {
        return new ResonanceContext(era, place, states, completed);
    }

    @Test
    void opensWhenAllConditionsMet() {
        var conditions = List.of(
                cond(ConditionType.ERA, "PARNAVAZ"),
                cond(ConditionType.PLACE, "throne_hall"));
        assertThat(evaluator.opens(conditions, at("PARNAVAZ", "throne_hall", Set.of(), Set.of()))).isTrue();
    }

    @Test
    void closedWhenOnePlaceConditionMissing() {
        var conditions = List.of(
                cond(ConditionType.ERA, "PARNAVAZ"),
                cond(ConditionType.PLACE, "throne_hall"));
        assertThat(evaluator.opens(conditions, at("PARNAVAZ", "market", Set.of(), Set.of()))).isFalse();
    }

    @Test
    void stateConditionMatchesActiveState() {
        var conditions = List.of(
                cond(ConditionType.PLACE, "mountain"),
                cond(ConditionType.STATE, "STORM"));
        assertThat(evaluator.opens(conditions, at("RUSTAVELI", "mountain", Set.of("STORM"), Set.of()))).isTrue();
        assertThat(evaluator.opens(conditions, at("RUSTAVELI", "mountain", Set.of("NIGHT"), Set.of()))).isFalse();
    }

    @Test
    void priorTaleConditionRequiresCompletion() {
        var conditions = List.of(cond(ConditionType.PRIOR_TALE, "TALE_FIRST_CROWN"));
        assertThat(evaluator.opens(conditions, at("EARLY_KING", null, Set.of(), Set.of("TALE_FIRST_CROWN")))).isTrue();
        assertThat(evaluator.opens(conditions, at("EARLY_KING", null, Set.of(), Set.of()))).isFalse();
    }

    @Test
    void emptyConditionsNeverOpen() {
        assertThat(evaluator.opens(List.of(), at("PARNAVAZ", "throne_hall", Set.of(), Set.of()))).isFalse();
    }
}
