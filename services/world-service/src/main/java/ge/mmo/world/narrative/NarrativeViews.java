package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Interaction;
import ge.mmo.world.narrative.NarrativeEnums.Tier;

import java.util.UUID;

/** Read models returned by the narrative engine to the web/WS layer. */
public final class NarrativeViews {
    private NarrativeViews() {
    }

    /** A Tale whose Resonance is open here (the Herald is present), with the Watcher's status. */
    public record ResonanceView(
            UUID taleId,
            String code,
            String title,
            Tier tier,
            String sagaTitle,
            String status) { // AVAILABLE | IN_PROGRESS | COMPLETED
    }

    public record BeatView(
            UUID id,
            String code,
            String narration,
            Interaction interaction,
            boolean terminal) {

        static BeatView of(Beat b) {
            return new BeatView(b.getId(), b.getCode(), b.getNarration(), b.getInteraction(), b.isTerminal());
        }
    }

    /** A public World Echo: the whole server's shared progress toward restoring a Tale. */
    public record WorldEchoView(
            UUID id,
            String taleCode,
            String taleTitle,
            String status,
            long progress,
            long goal,
            long participantCount) {
    }

    /** The live state of a Watcher inside a Tale. */
    public record TaleStateView(
            UUID taleId,
            String code,
            String title,
            String status,        // IN_PROGRESS | COMPLETED
            BeatView currentBeat, // null once completed
            Integer unlockedEraId) {
    }
}
