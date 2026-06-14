package ge.mmo.world.combat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface EnemyDefRepository extends JpaRepository<EnemyDef, UUID> {
    Optional<EnemyDef> findByCode(String code);

    List<EnemyDef> findAllByOrderByMaxHpAsc();
}

interface EncounterRepository extends JpaRepository<Encounter, UUID> {
    Optional<Encounter> findByCharacterIdAndStatus(UUID characterId, Encounter.Status status);
}
