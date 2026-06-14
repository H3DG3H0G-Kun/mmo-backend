package ge.mmo.world.party;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface PartyRepository extends JpaRepository<Party, UUID> {
}

interface PartyMemberRepository extends JpaRepository<PartyMember, UUID> {
    Optional<PartyMember> findByCharacterId(UUID characterId);

    List<PartyMember> findByPartyIdOrderByJoinedAtAsc(UUID partyId);

    long countByPartyId(UUID partyId);
}
