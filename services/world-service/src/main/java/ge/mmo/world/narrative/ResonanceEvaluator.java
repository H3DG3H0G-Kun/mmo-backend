package ge.mmo.world.narrative;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pure, framework-light evaluation of whether a Tale's Resonance opens in a given context.
 * A Tale opens iff EVERY one of its {@link TriggerCondition}s is satisfied (AND semantics).
 * This same condition logic backs resonance points, beat objectives, and era-gates.
 */
@Component
public class ResonanceEvaluator {

    /** True if all conditions hold for the context. A Tale with no conditions never opens. */
    public boolean opens(List<TriggerCondition> conditions, ResonanceContext ctx) {
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }
        return conditions.stream().allMatch(c -> satisfied(c, ctx));
    }

    private boolean satisfied(TriggerCondition c, ResonanceContext ctx) {
        return switch (c.getType()) {
            case ERA -> c.getValue().equals(ctx.eraCode());
            case PLACE -> c.getValue().equals(ctx.place());
            case STATE -> ctx.activeStates().contains(c.getValue());
            case PRIOR_TALE -> ctx.completedTaleCodes().contains(c.getValue());
        };
    }
}
