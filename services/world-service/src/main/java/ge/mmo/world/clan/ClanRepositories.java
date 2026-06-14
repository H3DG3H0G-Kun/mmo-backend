package ge.mmo.world.clan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ClanRepository extends JpaRepository<Clan, UUID> {
    @Query("select count(c) > 0 from Clan c where lower(c.name) = lower(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("select count(c) > 0 from Clan c where lower(c.tag) = lower(:tag)")
    boolean existsByTagIgnoreCase(@Param("tag") String tag);
}

interface ClanMemberRepository extends JpaRepository<ClanMember, UUID> {
    Optional<ClanMember> findByCharacterId(UUID characterId);

    Optional<ClanMember> findByClanIdAndCharacterId(UUID clanId, UUID characterId);

    List<ClanMember> findByClanIdOrderByJoinedAtAsc(UUID clanId);

    long countByClanId(UUID clanId);
}

interface ClanInviteRepository extends JpaRepository<ClanInvite, UUID> {
    Optional<ClanInvite> findByClanIdAndCharacterId(UUID clanId, UUID characterId);

    List<ClanInvite> findByClanId(UUID clanId);
}
