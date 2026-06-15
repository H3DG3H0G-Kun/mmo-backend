package ge.mmo.world.world;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByEraIdOrderByName(Integer eraId);

    Optional<Location> findByEraIdAndCode(Integer eraId, String code);
}

interface LocationLinkRepository extends JpaRepository<LocationLink, UUID> {
    boolean existsByFromLocationIdAndToLocationId(UUID fromLocationId, UUID toLocationId);

    List<LocationLink> findByFromLocationId(UUID fromLocationId);
}
