package ge.mmo.world.world;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NpcRepository extends JpaRepository<Npc, UUID> {
    List<Npc> findByEraId(Integer eraId);
}
