package ge.mmo.world.narrative;

import ge.mmo.world.narrative.NarrativeEnums.Interaction;
import ge.mmo.world.narrative.NarrativeEnums.Tier;

import java.util.List;
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

    /** One selectable branch out of a CHOOSE beat: the choice key + its display label. */
    public record ChoiceView(String key, String label) {
    }

    public record BeatView(
            UUID id,
            String code,
            String narration,
            Interaction interaction,
            boolean terminal,
            List<ChoiceView> choices) {

        static BeatView of(Beat b) {
            return of(b, List.of());
        }

        /** Builds the view, exposing the outgoing branches as choices only for CHOOSE beats. */
        static BeatView of(Beat b, List<BeatEdge> outgoing) {
            List<ChoiceView> choices = b.getInteraction() == Interaction.CHOOSE
                    ? outgoing.stream()
                    .filter(e -> e.getChoiceKey() != null && !e.getChoiceKey().isBlank())
                    .map(e -> new ChoiceView(e.getChoiceKey(), labelFor(e)))
                    .toList()
                    : List.of();
            return new BeatView(b.getId(), b.getCode(), b.getNarration(),
                    b.getInteraction(), b.isTerminal(), choices);
        }

        /** Authored label if present, else a humanized choice key. */
        private static String labelFor(BeatEdge e) {
            return (e.getLabel() != null && !e.getLabel().isBlank()) ? e.getLabel() : humanize(e.getChoiceKey());
        }

        /** "SOW_DOUBT" -> "Sow Doubt". Fallback when no authored label exists. */
        private static String humanize(String key) {
            String[] words = key.replace('_', ' ').trim().toLowerCase().split(" ");
            StringBuilder sb = new StringBuilder();
            for (String w : words) {
                if (w.isEmpty()) {
                    continue;
                }
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(' ');
            }
            return sb.isEmpty() ? key : sb.toString().trim();
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
