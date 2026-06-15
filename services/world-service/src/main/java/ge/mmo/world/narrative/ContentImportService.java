package ge.mmo.world.narrative;

import ge.mmo.world.narrative.ContentPackage.BeatDef;
import ge.mmo.world.narrative.ContentPackage.EdgeDef;
import ge.mmo.world.narrative.ContentPackage.TaleDef;
import ge.mmo.world.narrative.ContentPackage.TriggerDef;
import ge.mmo.world.narrative.NarrativeEnums.ConditionType;
import ge.mmo.world.narrative.NarrativeEnums.Interaction;
import ge.mmo.world.narrative.NarrativeEnums.Strand;
import ge.mmo.world.narrative.NarrativeEnums.Tier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Imports authored {@link ContentPackage} documents into the Saga/Tale/Beat graph. Insert-only by
 * code (re-importing an existing code is a conflict) — the data-driven authoring path that lets a
 * new tale be added with zero engine changes.
 */
@Service
public class ContentImportService {

    private final SagaRepository sagas;
    private final TaleRepository tales;
    private final BeatRepository beats;
    private final BeatEdgeRepository edges;
    private final TriggerConditionRepository conditions;

    public ContentImportService(SagaRepository sagas, TaleRepository tales, BeatRepository beats,
                                BeatEdgeRepository edges, TriggerConditionRepository conditions) {
        this.sagas = sagas;
        this.tales = tales;
        this.beats = beats;
        this.edges = edges;
        this.conditions = conditions;
    }

    public record ImportSummary(String sagaCode, int tales, int beats) {
    }

    @Transactional
    public ImportSummary importContent(ContentPackage pkg) {
        if (pkg.saga() == null || pkg.saga().code() == null) {
            throw new ContentValidationException("saga.code is required");
        }
        if (sagas.findByCode(pkg.saga().code()).isPresent()) {
            throw new ContentConflictException("Saga already exists: " + pkg.saga().code());
        }
        Strand strand = parse(Strand.class, pkg.saga().strand(), "saga.strand");
        Saga saga = sagas.save(new Saga(UUID.randomUUID(), pkg.saga().code(), pkg.saga().title(),
                strand, pkg.saga().eraId(), pkg.saga().ordinal()));

        int beatCount = 0;
        List<TaleDef> taleDefs = pkg.tales() == null ? List.of() : pkg.tales();
        for (TaleDef td : taleDefs) {
            if (td.code() == null) {
                throw new ContentValidationException("tale.code is required");
            }
            if (tales.findByCode(td.code()).isPresent()) {
                throw new ContentConflictException("Tale already exists: " + td.code());
            }
            Tier tier = parse(Tier.class, td.tier(), "tale.tier");
            Tale tale = tales.save(new Tale(UUID.randomUUID(), saga.getId(), td.code(), td.title(),
                    tier, td.ordinal(), td.unlocksEraId()));

            Map<String, UUID> beatIdByCode = new HashMap<>();
            for (BeatDef bd : nullSafe(td.beats())) {
                Interaction interaction = parse(Interaction.class, bd.interaction(), "beat.interaction");
                Beat beat = beats.save(new Beat(UUID.randomUUID(), tale.getId(), bd.code(),
                        bd.ordinal(), bd.narration(), interaction, bd.terminal()));
                beatIdByCode.put(bd.code(), beat.getId());
                beatCount++;
            }
            for (EdgeDef ed : nullSafe(td.edges())) {
                UUID from = requireBeat(beatIdByCode, ed.from(), td.code());
                UUID to = requireBeat(beatIdByCode, ed.to(), td.code());
                edges.save(new BeatEdge(UUID.randomUUID(), from, to, ed.choiceKey(), ed.label()));
            }
            for (TriggerDef tr : nullSafe(td.triggers())) {
                ConditionType type = parse(ConditionType.class, tr.type(), "trigger.type");
                conditions.save(new TriggerCondition(UUID.randomUUID(), tale.getId(), type, tr.value()));
            }
        }
        return new ImportSummary(saga.getCode(), taleDefs.size(), beatCount);
    }

    private static <E extends Enum<E>> E parse(Class<E> type, String value, String field) {
        if (value == null) {
            throw new ContentValidationException(field + " is required");
        }
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException e) {
            throw new ContentValidationException("Invalid " + field + ": " + value);
        }
    }

    private static UUID requireBeat(Map<String, UUID> beatIds, String code, String taleCode) {
        UUID id = beatIds.get(code);
        if (id == null) {
            throw new ContentValidationException("Edge references unknown beat '" + code + "' in tale " + taleCode);
        }
        return id;
    }

    private static <T> List<T> nullSafe(List<T> list) {
        return list == null ? List.of() : list;
    }
}
