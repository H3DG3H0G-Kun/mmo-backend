package ge.mmo.world.siege;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface PlaceOfPowerRepository extends JpaRepository<PlaceOfPower, UUID> {
    Optional<PlaceOfPower> findByCode(String code);

    List<PlaceOfPower> findAllByOrderByEraIdAscNameAsc();
}

interface SiegeRepository extends JpaRepository<Siege, UUID> {
    // The open (non-resolved) siege for a place, if any.
    Optional<Siege> findByPlaceIdAndStatusNot(UUID placeId, Siege.Status status);
}

interface SiegeParticipantRepository extends JpaRepository<SiegeParticipant, UUID> {
    Optional<SiegeParticipant> findBySiegeIdAndClanId(UUID siegeId, UUID clanId);

    List<SiegeParticipant> findBySiegeIdOrderByScoreDesc(UUID siegeId);
}
