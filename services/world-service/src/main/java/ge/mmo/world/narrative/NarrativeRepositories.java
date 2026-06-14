package ge.mmo.world.narrative;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SagaRepository extends JpaRepository<Saga, UUID> {
    Optional<Saga> findByCode(String code);
}

interface TaleRepository extends JpaRepository<Tale, UUID> {
    Optional<Tale> findByCode(String code);
}

interface BeatRepository extends JpaRepository<Beat, UUID> {
    List<Beat> findByTaleIdOrderByOrdinalAsc(UUID taleId);

    Optional<Beat> findFirstByTaleIdOrderByOrdinalAsc(UUID taleId);
}

interface BeatEdgeRepository extends JpaRepository<BeatEdge, UUID> {
    List<BeatEdge> findByFromBeatId(UUID fromBeatId);
}

interface TriggerConditionRepository extends JpaRepository<TriggerCondition, UUID> {
    List<TriggerCondition> findByTaleId(UUID taleId);
}

interface TaleProgressRepository extends JpaRepository<TaleProgress, UUID> {
    Optional<TaleProgress> findByCharacterIdAndTaleId(UUID characterId, UUID taleId);

    List<TaleProgress> findByCharacterId(UUID characterId);
}

interface TaleInstanceRepository extends JpaRepository<TaleInstance, UUID> {
    Optional<TaleInstance> findByPartyIdAndStatus(UUID partyId, TaleInstance.Status status);
}

interface WorldEchoRepository extends JpaRepository<WorldEcho, UUID> {
    Optional<WorldEcho> findByTaleIdAndStatus(UUID taleId, WorldEcho.Status status);

    List<WorldEcho> findByStatus(WorldEcho.Status status);
}

interface WorldEchoParticipationRepository extends JpaRepository<WorldEchoParticipation, UUID> {
    Optional<WorldEchoParticipation> findByWorldEchoIdAndCharacterId(UUID worldEchoId, UUID characterId);

    List<WorldEchoParticipation> findByWorldEchoId(UUID worldEchoId);

    long countByWorldEchoId(UUID worldEchoId);
}
