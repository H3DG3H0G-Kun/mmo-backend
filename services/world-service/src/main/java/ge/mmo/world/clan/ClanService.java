package ge.mmo.world.clan;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.clan.ClanViews.ClanMemberView;
import ge.mmo.world.clan.ClanViews.ClanView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Clans — persistent Orders of Watchers. Ranked membership (LEADER/OFFICER/MEMBER), invite-based
 * joining, backend-validated rank rules. The foundation for the future siege/territory system.
 */
@Service
public class ClanService {

    private static final int DEFAULT_MAX = 30;
    private static final int MIN_MAX = 5;
    private static final int MAX_MAX = 100;

    private final ClanRepository clans;
    private final ClanMemberRepository members;
    private final ClanInviteRepository invites;
    private final CharacterService characters;

    public ClanService(ClanRepository clans, ClanMemberRepository members,
                       ClanInviteRepository invites, CharacterService characters) {
        this.clans = clans;
        this.members = members;
        this.invites = invites;
        this.characters = characters;
    }

    @Transactional
    public ClanView create(UUID accountId, UUID characterId, String name, String tag, Integer requestedMax) {
        characters.requireOwned(accountId, characterId);
        if (members.findByCharacterId(characterId).isPresent()) {
            throw new AlreadyInClanException();
        }
        if (clans.existsByNameIgnoreCase(name)) {
            throw new ClanNameTakenException(name);
        }
        if (clans.existsByTagIgnoreCase(tag)) {
            throw new ClanTagTakenException(tag);
        }
        int max = Math.max(MIN_MAX, Math.min(MAX_MAX, requestedMax == null ? DEFAULT_MAX : requestedMax));
        Clan clan = clans.save(new Clan(UUID.randomUUID(), name, tag, characterId, max));
        members.save(new ClanMember(UUID.randomUUID(), clan.getId(), characterId, ClanRank.LEADER));
        return view(clan);
    }

    /** Officer or leader invites a character who is not yet in a clan. */
    @Transactional
    public ClanView invite(UUID accountId, UUID actorCharacterId, UUID targetCharacterId) {
        characters.requireOwned(accountId, actorCharacterId);
        ClanMember actor = members.findByCharacterId(actorCharacterId).orElseThrow(NotInClanException::new);
        requireRank(actor, ClanRank.OFFICER);
        if (members.findByCharacterId(targetCharacterId).isPresent()) {
            throw new AlreadyInClanException();
        }
        if (invites.findByClanIdAndCharacterId(actor.getClanId(), targetCharacterId).isEmpty()) {
            invites.save(new ClanInvite(UUID.randomUUID(), actor.getClanId(), targetCharacterId, actorCharacterId));
        }
        return view(requireClan(actor.getClanId()));
    }

    /** Accept a pending invite to a clan. */
    @Transactional
    public ClanView accept(UUID accountId, UUID characterId, UUID clanId) {
        characters.requireOwned(accountId, characterId);
        if (members.findByCharacterId(characterId).isPresent()) {
            throw new AlreadyInClanException();
        }
        ClanInvite invite = invites.findByClanIdAndCharacterId(clanId, characterId)
                .orElseThrow(NoClanInviteException::new);
        Clan clan = requireClan(clanId);
        if (members.countByClanId(clanId) >= clan.getMaxMembers()) {
            throw new ClanFullException();
        }
        members.save(new ClanMember(UUID.randomUUID(), clanId, characterId, ClanRank.MEMBER));
        invites.delete(invite);
        return view(clan);
    }

    /** Leave the clan; promotes a new leader, or disbands the clan when the last member leaves. */
    @Transactional
    public Optional<ClanView> leave(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        ClanMember membership = members.findByCharacterId(characterId).orElseThrow(NotInClanException::new);
        Clan clan = requireClan(membership.getClanId());

        members.delete(membership);
        List<ClanMember> remaining = members.findByClanIdOrderByJoinedAtAsc(clan.getId());
        if (remaining.isEmpty()) {
            invites.deleteAll(invites.findByClanId(clan.getId()));
            clans.delete(clan);
            return Optional.empty();
        }
        if (clan.getLeaderCharacterId().equals(characterId)) {
            ClanMember successor = remaining.stream()
                    .max(Comparator.comparingInt(m -> m.getRank().ordinal()))
                    .orElseThrow();
            successor.setRank(ClanRank.LEADER);
            members.save(successor);
            clan.setLeader(successor.getCharacterId());
            clans.save(clan);
        }
        return Optional.of(view(clan));
    }

    /** Officer/leader removes a lower-ranked member. */
    @Transactional
    public ClanView kick(UUID accountId, UUID actorCharacterId, UUID targetCharacterId) {
        characters.requireOwned(accountId, actorCharacterId);
        ClanMember actor = members.findByCharacterId(actorCharacterId).orElseThrow(NotInClanException::new);
        requireRank(actor, ClanRank.OFFICER);
        ClanMember target = members.findByClanIdAndCharacterId(actor.getClanId(), targetCharacterId)
                .orElseThrow(NotInClanException::new);
        if (!actor.getRank().outranks(target.getRank())) {
            throw new InsufficientClanRankException();
        }
        members.delete(target);
        return view(requireClan(actor.getClanId()));
    }

    /** Leader sets another member's rank. Setting a member to LEADER transfers leadership. */
    @Transactional
    public ClanView setRank(UUID accountId, UUID leaderCharacterId, UUID targetCharacterId, ClanRank newRank) {
        characters.requireOwned(accountId, leaderCharacterId);
        ClanMember leader = members.findByCharacterId(leaderCharacterId).orElseThrow(NotInClanException::new);
        if (leader.getRank() != ClanRank.LEADER) {
            throw new InsufficientClanRankException();
        }
        if (leaderCharacterId.equals(targetCharacterId)) {
            throw new InsufficientClanRankException();
        }
        ClanMember target = members.findByClanIdAndCharacterId(leader.getClanId(), targetCharacterId)
                .orElseThrow(NotInClanException::new);
        Clan clan = requireClan(leader.getClanId());

        if (newRank == ClanRank.LEADER) {
            target.setRank(ClanRank.LEADER);
            leader.setRank(ClanRank.OFFICER);
            members.save(leader);
            clan.setLeader(target.getCharacterId());
            clans.save(clan);
        } else {
            target.setRank(newRank);
        }
        members.save(target);
        return view(clan);
    }

    @Transactional(readOnly = true)
    public ClanView myClan(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        ClanMember membership = members.findByCharacterId(characterId).orElseThrow(NotInClanException::new);
        return view(requireClan(membership.getClanId()));
    }

    /** The clan this character belongs to, if any. */
    @Transactional(readOnly = true)
    public Optional<UUID> clanIdOf(UUID characterId) {
        return members.findByCharacterId(characterId).map(ClanMember::getClanId);
    }

    /** True if this character is the LEADER of their clan. */
    @Transactional(readOnly = true)
    public boolean isClanLeader(UUID characterId) {
        return members.findByCharacterId(characterId)
                .map(m -> m.getRank() == ClanRank.LEADER)
                .orElse(false);
    }

    private void requireRank(ClanMember member, ClanRank min) {
        if (!member.getRank().atLeast(min)) {
            throw new InsufficientClanRankException();
        }
    }

    private Clan requireClan(UUID clanId) {
        return clans.findById(clanId).orElseThrow(ClanNotFoundException::new);
    }

    private ClanView view(Clan clan) {
        List<ClanMember> roster = members.findByClanIdOrderByJoinedAtAsc(clan.getId());
        Map<UUID, String> names = characters.byIds(roster.stream().map(ClanMember::getCharacterId).toList())
                .stream().collect(Collectors.toMap(PlayerCharacter::getId, PlayerCharacter::getName));
        List<ClanMemberView> memberViews = roster.stream()
                .map(m -> new ClanMemberView(m.getCharacterId(),
                        names.getOrDefault(m.getCharacterId(), "?"), m.getRank()))
                .toList();
        return new ClanView(clan.getId(), clan.getName(), clan.getTag(),
                clan.getLeaderCharacterId(), clan.getMaxMembers(), memberViews);
    }
}
