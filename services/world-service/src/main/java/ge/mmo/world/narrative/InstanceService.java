package ge.mmo.world.narrative;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.narrative.NarrativeEnums.Progress;
import ge.mmo.world.narrative.NarrativeViews.BeatView;
import ge.mmo.world.narrative.NarrativeViews.TaleStateView;
import ge.mmo.world.party.PartyService;
import ge.mmo.world.timeline.TimelineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Co-op runs of a Tale: a party enters one shared instance, any member advances the shared
 * beat, and on completion the reward (era unlock) fans out to every member. The server records
 * a single restoration on the Living Timeline. Backend-validated end to end.
 */
@Service
public class InstanceService {

    private final TaleRepository tales;
    private final BeatRepository beats;
    private final BeatEdgeRepository edges;
    private final TriggerConditionRepository conditions;
    private final SagaRepository sagas;
    private final TaleProgressRepository progressRepo;
    private final TaleInstanceRepository instances;
    private final ResonanceEvaluator evaluator;
    private final ResonanceContextFactory contextFactory;
    private final BeatNavigator navigator;
    private final CharacterService characters;
    private final PartyService parties;
    private final TimelineService timeline;

    public InstanceService(TaleRepository tales, BeatRepository beats, BeatEdgeRepository edges,
                           TriggerConditionRepository conditions, SagaRepository sagas,
                           TaleProgressRepository progressRepo, TaleInstanceRepository instances,
                           ResonanceEvaluator evaluator, ResonanceContextFactory contextFactory,
                           BeatNavigator navigator, CharacterService characters,
                           PartyService parties, TimelineService timeline) {
        this.tales = tales;
        this.beats = beats;
        this.edges = edges;
        this.conditions = conditions;
        this.sagas = sagas;
        this.progressRepo = progressRepo;
        this.instances = instances;
        this.evaluator = evaluator;
        this.contextFactory = contextFactory;
        this.navigator = navigator;
        this.characters = characters;
        this.parties = parties;
        this.timeline = timeline;
    }

    /** The party leader begins a co-op run, validated by the leader's Resonance context. */
    @Transactional
    public TaleStateView start(UUID accountId, UUID characterId, String taleCode,
                               String place, java.util.Set<String> states) {
        PlayerCharacter leader = characters.requireOwned(accountId, characterId);
        UUID partyId = parties.partyIdOf(characterId).orElseThrow(PartyRequiredException::new);
        if (!parties.isLeader(partyId, characterId)) {
            throw new NotInstanceLeaderException();
        }
        if (instances.findByPartyIdAndStatus(partyId, TaleInstance.Status.ACTIVE).isPresent()) {
            throw new InstanceAlreadyActiveException();
        }
        Tale tale = tales.findByCode(taleCode).orElseThrow(() -> new TaleNotFoundException(taleCode));
        ResonanceContext ctx = contextFactory.forCharacter(leader, place, states);
        if (!evaluator.opens(conditions.findByTaleId(tale.getId()), ctx)) {
            throw new ResonanceClosedException(taleCode);
        }
        Beat first = beats.findFirstByTaleIdOrderByOrdinalAsc(tale.getId())
                .orElseThrow(() -> new TaleNotFoundException(taleCode + " (no beats)"));
        instances.save(new TaleInstance(UUID.randomUUID(), tale.getId(), partyId, first.getId()));
        return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                TaleInstance.Status.ACTIVE.name(), BeatView.of(first), null);
    }

    /** Any party member advances the shared beat; completing it rewards the whole party. */
    @Transactional
    public TaleStateView advance(UUID accountId, UUID characterId, String choiceKey) {
        characters.requireOwned(accountId, characterId);
        UUID partyId = parties.partyIdOf(characterId).orElseThrow(PartyRequiredException::new);
        TaleInstance instance = instances.findByPartyIdAndStatus(partyId, TaleInstance.Status.ACTIVE)
                .orElseThrow(NoActiveInstanceException::new);
        Tale tale = tales.findById(instance.getTaleId())
                .orElseThrow(() -> new TaleNotFoundException(instance.getTaleId().toString()));
        Beat current = beats.findById(instance.getCurrentBeatId())
                .orElseThrow(() -> new TaleNotFoundException(tale.getCode() + " (beat missing)"));

        if (current.isTerminal()) {
            instance.complete();
            instances.save(instance);
            rewardParty(partyId, tale);
            return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                    TaleInstance.Status.COMPLETED.name(), null, tale.getUnlocksEraId());
        }

        var chosen = navigator.selectEdge(current, edges.findByFromBeatId(current.getId()), choiceKey);
        Beat next = beats.findById(chosen.getToBeatId())
                .orElseThrow(() -> new TaleNotFoundException(tale.getCode() + " (next beat missing)"));
        instance.moveTo(next.getId());
        instances.save(instance);
        return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                TaleInstance.Status.ACTIVE.name(), BeatView.of(next), null);
    }

    @Transactional(readOnly = true)
    public TaleStateView stateForParty(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        UUID partyId = parties.partyIdOf(characterId).orElseThrow(PartyRequiredException::new);
        TaleInstance instance = instances.findByPartyIdAndStatus(partyId, TaleInstance.Status.ACTIVE)
                .orElseThrow(NoActiveInstanceException::new);
        Tale tale = tales.findById(instance.getTaleId())
                .orElseThrow(() -> new TaleNotFoundException(instance.getTaleId().toString()));
        BeatView beat = instance.getCurrentBeatId() == null ? null
                : beats.findById(instance.getCurrentBeatId()).map(BeatView::of).orElse(null);
        return new TaleStateView(tale.getId(), tale.getCode(), tale.getTitle(),
                instance.getStatus().name(), beat, null);
    }

    /** Fan out the completion reward to every current party member; record one restoration. */
    private void rewardParty(UUID partyId, Tale tale) {
        List<UUID> memberIds = parties.memberCharacterIds(partyId);
        for (UUID memberId : memberIds) {
            TaleProgress progress = progressRepo.findByCharacterIdAndTaleId(memberId, tale.getId())
                    .orElseGet(() -> new TaleProgress(UUID.randomUUID(), memberId, tale.getId(), null));
            if (progress.getStatus() != Progress.COMPLETED) {
                progress.complete();
                progressRepo.save(progress);
            }
            if (tale.getUnlocksEraId() != null) {
                timeline.unlockEra(memberId, tale.getUnlocksEraId());
            }
        }
        sagas.findById(tale.getSagaId()).ifPresent(s -> timeline.recordRestoration(s.getEraId()));
    }
}
