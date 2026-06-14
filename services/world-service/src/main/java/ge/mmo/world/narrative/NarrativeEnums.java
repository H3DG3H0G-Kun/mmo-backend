package ge.mmo.world.narrative;

/**
 * Vocabulary of the narrative engine (see docs/GAME_DESIGN.md). A {@code Tale} here is the
 * design's "Thread" — renamed only to avoid clashing with {@code java.lang.Thread}.
 */
public final class NarrativeEnums {
    private NarrativeEnums() {
    }

    /** The three strands of memory — all modeled by the same engine. */
    public enum Strand { HISTORY, MYTH, WORD }

    /** True tales keep the memory as it was; Echo tales are explicit "what-if"/corrupted memory. */
    public enum Tier { TRUE_TALE, ECHO }

    /** How the Watcher may act in a Beat — never replacing the hero. */
    public enum Interaction { WITNESS, AID, WARD, CHOOSE, RESTORE }

    /** A single composable trigger condition. A Tale opens when ALL its conditions hold. */
    public enum ConditionType {
        ERA,          // value = era code the Watcher must be in
        PLACE,        // value = world-anchor code the Watcher must be at
        STATE,        // value = an active world/environment state (e.g. STORM)
        PRIOR_TALE    // value = a Tale code that must already be completed
    }

    /** Lifecycle of a Watcher's run through a Tale. */
    public enum Progress { IN_PROGRESS, COMPLETED }
}
