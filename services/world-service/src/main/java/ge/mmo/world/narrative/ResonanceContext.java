package ge.mmo.world.narrative;

import java.util.Set;

/**
 * The Watcher's current world situation, against which Resonance conditions are evaluated.
 * The same generic context answers "which Tales may open here?" — place, era, active states,
 * and which Tales this Watcher has already completed.
 */
public record ResonanceContext(
        String eraCode,
        String place,
        Set<String> activeStates,
        Set<String> completedTaleCodes) {

    public ResonanceContext {
        activeStates = activeStates == null ? Set.of() : Set.copyOf(activeStates);
        completedTaleCodes = completedTaleCodes == null ? Set.of() : Set.copyOf(completedTaleCodes);
    }
}
