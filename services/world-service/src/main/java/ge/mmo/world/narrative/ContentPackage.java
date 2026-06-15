package ge.mmo.world.narrative;

import java.util.List;

/**
 * The authored shape of narrative content (a Saga and its Tales) for JSON/YAML import — the
 * "stories are data" pipeline. A whole new poem/chronicle is one of these documents; no SQL,
 * no engine code. Strand/Tier/Interaction/ConditionType are the enum names from {@link NarrativeEnums}.
 */
public record ContentPackage(SagaDef saga, List<TaleDef> tales) {

    public record SagaDef(String code, String title, String strand, Integer eraId, int ordinal) {
    }

    public record TaleDef(
            String code,
            String title,
            String tier,
            int ordinal,
            Integer unlocksEraId,
            List<BeatDef> beats,
            List<EdgeDef> edges,
            List<TriggerDef> triggers) {
    }

    public record BeatDef(String code, int ordinal, String narration, String interaction, boolean terminal) {
    }

    /**
     * A directed edge between beats (by code). {@code choiceKey} only for CHOOSE beats;
     * {@code label} is the optional authored button text (null → humanized key).
     */
    public record EdgeDef(String from, String to, String choiceKey, String label) {
    }

    public record TriggerDef(String type, String value) {
    }
}
