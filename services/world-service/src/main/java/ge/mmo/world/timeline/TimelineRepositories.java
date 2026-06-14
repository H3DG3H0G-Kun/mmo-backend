package ge.mmo.world.timeline;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface EraUnlockRepository extends JpaRepository<EraUnlock, UUID> {
    boolean existsByCharacterIdAndEraId(UUID characterId, Integer eraId);

    List<EraUnlock> findByCharacterId(UUID characterId);
}

interface TimelineProgressRepository extends JpaRepository<TimelineProgress, Integer> {
}
