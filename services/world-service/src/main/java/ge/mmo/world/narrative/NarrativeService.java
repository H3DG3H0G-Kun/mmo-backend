package ge.mmo.world.narrative;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.timeline.TimelineService;
import ge.mmo.world.narrative.NarrativeEnums.Interaction;
import ge.mmo.world.narrative.NarrativeEnums.Progress;
import ge.mmo.world.narrative.NarrativeViews.BeatView;
import ge.mmo.world.narrative.NarrativeViews.ResonanceView;
import ge.mmo.world.narrative.NarrativeViews.TaleStateView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The narrative engine. Generic over content: it reads the Saga/Tale/Beat graph and the
 * composable Resonance conditions from data and never names a specific story. The backend
 * decides what opens, what may be entered, and how a Tale advances — the client only intends.
 */
@Service
public class NarrativeService {

    private final SagaRepository sagas;
    private final TaleRepository tales;
    private final BeatRepository beats;
    private final BeatEdgeRepository edges;
    private final TriggerConditionRepository conditions;
    private final TaleProgressRepository progressRepo;
    private final ResonanceEvaluator evaluator;
    private final CharacterService characters;
    private final TimelineService timeline;

    public NarrativeService(SagaRepository sagas, TaleRepository tales, BeatRepository beats,
                            BeatEdgeRepository edges, TriggerConditionRepository conditions,
                            TaleProgressRepository progressRepo, ResonanceEvaluator evaluator,
                            CharacterService characters, TimelineService timeline) {
        this.sagas = sagas;
        this.tales = tales;
        this.beats = beats;
        this.edges = edges;
        this.conditions = conditions;
        this.progressRepo = progressRepo;
        this.evaluator = evaluator;
        this.characters = characters;
        this.timeline = timeline;
    }

    /** Which Tales resonate (the Herald appears) for this Watcher, here and now. */
    @Transactional(readOnly = true)
    public List<ResonanceView> availableResonances(UUID accountId, UUID characterId,
                                                   String place, Set<String> states) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        ResonanceContext ctx = contextFor(character, place, states);
        Map<UUID, Progress> statusByTale = progressRepo.findByCharacterId(characterId).stream()
                .collect(Collectors.toMap(TaleProgress::getTaleId, TaleProgress::getStatus));

        List<ResonanceView> open = new ArrayList<>();
        for (Tale tale : tales.findAll()) {
            if (evaluator.opens(conditions.findByTaleId(tale.getId()), ctx)) {
                String status = statusByTale.getOrDefault(tale.getId(), null) == null
                        ? "AVAILABLE" : statusByTale.get(tale.getId()).name();
                String sagaTitle = sagas.findById(tale.getSagaId()).map(Saga::getTitle).orElse(null);
                open.add(new ResonanceView(tale.getId(), tale.getCode(), tale.getTitle(),
                        tale.getTier(), sagaTitle, status));
            }
        }
        return open;
    }

    /** Enter a Tale (resume if already in progress). Validates the Resonance is open. */
    @Transactional
    public TaleStateView enter(UUID accountId, UUID characterId, String taleCode,
                               String place, Set<String> states) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        Tale tale = tales.findByCode(taleCode).orElseThrow(() -> new TaleNotFoundException(taleCode));

        var existing = progressRepo.findByCharacterIdAndTaleId(characterId, tale.getId());
        if (existing.isPresent()) {
            return stateOf(tale, existing.get());
        }

        ResonanceContext ctx = contextFor(character, place, states);
        if (!evaluator.opens(conditions.findByTaleId(tale.getId()), ctx)) {
            throw new ResonanceClosedException(taleCode);
        }
        Beat first = beats.findFirstByTaleIdOrderByOrdinalAsc(tale.getId())
                .orElseThrow(() -> new TaleNotFoundException(taleCode + " (no beats)"));
        TaleProgress progress = new TaleProgress(UUID.randomUUID(), characterId, tale.getId(), first.getId());
        progressRepo.save(progress);
        return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                progress.getStatus().name(), BeatView.of(first), null);
    }

    /**
     * Perform the current Beat's interaction and move on. WITNESS/AID/WARD/RESTORE advance along
     * the single outgoing edge; CHOOSE requires a choiceKey. Advancing from a terminal Beat
     * completes the Tale and applies its reward (era unlock).
     */
    @Transactional
    public TaleStateView advance(UUID accountId, UUID characterId, String taleCode, String choiceKey) {
        characters.requireOwned(accountId, characterId); // ownership check
        Tale tale = tales.findByCode(taleCode).orElseThrow(() -> new TaleNotFoundException(taleCode));
        TaleProgress progress = progressRepo.findByCharacterIdAndTaleId(characterId, tale.getId())
                .filter(p -> p.getStatus() == Progress.IN_PROGRESS)
                .orElseThrow(() -> new NoActiveProgressException(taleCode));

        Beat current = beats.findById(progress.getCurrentBeatId())
                .orElseThrow(() -> new TaleNotFoundException(taleCode + " (beat missing)"));

        if (current.isTerminal()) {
            progress.complete();
            progressRepo.save(progress);
            // Reward + Living Timeline: unlock the era this Tale opens, and record the restoration.
            if (tale.getUnlocksEraId() != null) {
                timeline.unlockEra(characterId, tale.getUnlocksEraId());
            }
            sagas.findById(tale.getSagaId()).ifPresent(s -> timeline.recordRestoration(s.getEraId()));
            return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                    progress.getStatus().name(), null, tale.getUnlocksEraId());
        }

        List<BeatEdge> outgoing = edges.findByFromBeatId(current.getId());
        BeatEdge chosen = selectEdge(current, outgoing, choiceKey);
        Beat next = beats.findById(chosen.getToBeatId())
                .orElseThrow(() -> new TaleNotFoundException(taleCode + " (next beat missing)"));
        progress.moveTo(next.getId());
        progressRepo.save(progress);
        return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                progress.getStatus().name(), BeatView.of(next), null);
    }

    private BeatEdge selectEdge(Beat current, List<BeatEdge> outgoing, String choiceKey) {
        if (outgoing.isEmpty()) {
            throw new InvalidChoiceException("Beat " + current.getCode() + " has no outgoing edge");
        }
        if (current.getInteraction() == Interaction.CHOOSE) {
            if (choiceKey == null || choiceKey.isBlank()) {
                throw new InvalidChoiceException("This beat requires a choiceKey");
            }
            return outgoing.stream()
                    .filter(e -> choiceKey.equals(e.getChoiceKey()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidChoiceException("No branch for choiceKey: " + choiceKey));
        }
        return outgoing.getFirst();
    }

    private TaleStateView stateOf(Tale tale, TaleProgress progress) {
        BeatView beat = progress.getCurrentBeatId() == null ? null
                : beats.findById(progress.getCurrentBeatId()).map(BeatView::of).orElse(null);
        return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                progress.getStatus().name(),
                progress.getStatus() == Progress.COMPLETED ? null : beat,
                progress.getStatus() == Progress.COMPLETED ? tale.getUnlocksEraId() : null);
    }

    private ResonanceContext contextFor(PlayerCharacter character, String place, Set<String> states) {
        List<TaleProgress> all = progressRepo.findByCharacterId(character.getId());
        Set<UUID> completedIds = all.stream()
                .filter(p -> p.getStatus() == Progress.COMPLETED)
                .map(TaleProgress::getTaleId)
                .collect(Collectors.toSet());
        Set<String> completedCodes = tales.findAllById(completedIds).stream()
                .map(Tale::getCode)
                .collect(Collectors.toSet());
        return new ResonanceContext(character.getCurrentEra().getCode(), place, states, completedCodes);
    }
}
