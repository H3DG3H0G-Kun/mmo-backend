package ge.mmo.world.narrative;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.narrative.NarrativeEnums.Progress;
import ge.mmo.world.narrative.NarrativeViews.WorldEchoView;
import ge.mmo.world.timeline.TimelineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * World Echoes: public, server-shared runs of a Tale. Any Watcher contributes; when the
 * collective progress reaches the goal, the memory is restored for EVERY participant (era unlock
 * + a single Living-Timeline restoration). The communal counterpart to solo/party instances.
 */
@Service
public class WorldEchoService {

    static final long DEFAULT_GOAL = 100;

    private final TaleRepository tales;
    private final SagaRepository sagas;
    private final TriggerConditionRepository conditions;
    private final TaleProgressRepository progressRepo;
    private final WorldEchoRepository echoes;
    private final WorldEchoParticipationRepository participations;
    private final ResonanceEvaluator evaluator;
    private final ResonanceContextFactory contextFactory;
    private final CharacterService characters;
    private final TimelineService timeline;

    public WorldEchoService(TaleRepository tales, SagaRepository sagas,
                            TriggerConditionRepository conditions, TaleProgressRepository progressRepo,
                            WorldEchoRepository echoes, WorldEchoParticipationRepository participations,
                            ResonanceEvaluator evaluator, ResonanceContextFactory contextFactory,
                            CharacterService characters, TimelineService timeline) {
        this.tales = tales;
        this.sagas = sagas;
        this.conditions = conditions;
        this.progressRepo = progressRepo;
        this.echoes = echoes;
        this.participations = participations;
        this.evaluator = evaluator;
        this.contextFactory = contextFactory;
        this.characters = characters;
        this.timeline = timeline;
    }

    /** Summon a public Echo of a Tale (validated by the summoner's Resonance context). */
    @Transactional
    public WorldEchoView summon(UUID accountId, UUID characterId, String taleCode,
                                String place, Set<String> states) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        Tale tale = tales.findByCode(taleCode).orElseThrow(() -> new TaleNotFoundException(taleCode));
        if (echoes.findByTaleIdAndStatus(tale.getId(), WorldEcho.Status.ACTIVE).isPresent()) {
            throw new WorldEchoActiveException();
        }
        ResonanceContext ctx = contextFactory.forCharacter(character, place, states);
        if (!evaluator.opens(conditions.findByTaleId(tale.getId()), ctx)) {
            throw new ResonanceClosedException(taleCode);
        }
        WorldEcho echo = echoes.save(new WorldEcho(UUID.randomUUID(), tale.getId(), DEFAULT_GOAL));
        participations.save(new WorldEchoParticipation(UUID.randomUUID(), echo.getId(), characterId));
        return view(echo, tale);
    }

    /** Any Watcher contributes to an active Echo; reaching the goal restores it for everyone. */
    @Transactional
    public WorldEchoView contribute(UUID accountId, UUID characterId, UUID echoId, long points) {
        characters.requireOwned(accountId, characterId);
        WorldEcho echo = echoes.findById(echoId).orElseThrow(WorldEchoNotFoundException::new);
        if (echo.getStatus() != WorldEcho.Status.ACTIVE) {
            throw new WorldEchoNotActiveException();
        }
        WorldEchoParticipation part = participations.findByWorldEchoIdAndCharacterId(echoId, characterId)
                .orElseGet(() -> new WorldEchoParticipation(UUID.randomUUID(), echoId, characterId));
        long pts = Math.max(1, points);
        part.addContribution(pts);
        participations.save(part);
        echo.addProgress(pts);

        Tale tale = tales.findById(echo.getTaleId())
                .orElseThrow(() -> new TaleNotFoundException(echo.getTaleId().toString()));
        if (echo.goalReached()) {
            resolveAndReward(echo, tale);
        }
        echoes.save(echo);
        return view(echo, tale);
    }

    @Transactional(readOnly = true)
    public List<WorldEchoView> listActive() {
        return echoes.findByStatus(WorldEcho.Status.ACTIVE).stream()
                .map(e -> view(e, tales.findById(e.getTaleId()).orElse(null)))
                .toList();
    }

    @Transactional(readOnly = true)
    public WorldEchoView get(UUID echoId) {
        WorldEcho echo = echoes.findById(echoId).orElseThrow(WorldEchoNotFoundException::new);
        return view(echo, tales.findById(echo.getTaleId()).orElse(null));
    }

    /** Fan the restoration out to every participant; record one Living-Timeline restoration. */
    private void resolveAndReward(WorldEcho echo, Tale tale) {
        echo.resolve();
        for (WorldEchoParticipation p : participations.findByWorldEchoId(echo.getId())) {
            UUID cid = p.getCharacterId();
            TaleProgress tp = progressRepo.findByCharacterIdAndTaleId(cid, tale.getId())
                    .orElseGet(() -> new TaleProgress(UUID.randomUUID(), cid, tale.getId(), null));
            if (tp.getStatus() != Progress.COMPLETED) {
                tp.complete();
                progressRepo.save(tp);
            }
            if (tale.getUnlocksEraId() != null) {
                timeline.unlockEra(cid, tale.getUnlocksEraId());
            }
        }
        sagas.findById(tale.getSagaId()).ifPresent(s -> timeline.recordRestoration(s.getEraId()));
    }

    private WorldEchoView view(WorldEcho echo, Tale tale) {
        return new WorldEchoView(echo.getId(),
                tale == null ? null : tale.getCode(),
                tale == null ? null : tale.getTitle(),
                echo.getStatus().name(),
                echo.getProgress(), echo.getGoal(),
                participations.countByWorldEchoId(echo.getId()));
    }
}
