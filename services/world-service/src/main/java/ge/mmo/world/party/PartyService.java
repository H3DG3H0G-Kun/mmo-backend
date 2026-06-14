package ge.mmo.world.party;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.party.PartyViews.MemberView;
import ge.mmo.world.party.PartyViews.PartyView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Parties — the small groups of Watchers that the shared-world experience is built around.
 * Every action is backend-validated: a character is in at most one party, only the leader may
 * disband, and a closed/full party cannot be joined.
 */
@Service
public class PartyService {

    private static final int DEFAULT_MAX = 5;
    private static final int MIN_MAX = 2;
    private static final int MAX_MAX = 10;

    private final PartyRepository parties;
    private final PartyMemberRepository members;
    private final CharacterService characters;

    public PartyService(PartyRepository parties, PartyMemberRepository members, CharacterService characters) {
        this.parties = parties;
        this.members = members;
        this.characters = characters;
    }

    @Transactional
    public PartyView create(UUID accountId, UUID characterId, Integer requestedMax) {
        characters.requireOwned(accountId, characterId);
        requireNotInParty(characterId);
        int maxSize = Math.max(MIN_MAX, Math.min(MAX_MAX, requestedMax == null ? DEFAULT_MAX : requestedMax));

        Party party = parties.save(new Party(UUID.randomUUID(), characterId, maxSize));
        members.save(new PartyMember(UUID.randomUUID(), party.getId(), characterId));
        return view(party);
    }

    @Transactional
    public PartyView join(UUID accountId, UUID characterId, UUID partyId) {
        characters.requireOwned(accountId, characterId);
        requireNotInParty(characterId);
        Party party = parties.findById(partyId).orElseThrow(PartyNotFoundException::new);
        if (party.getStatus() != Party.Status.OPEN) {
            throw new PartyClosedException();
        }
        if (members.countByPartyId(partyId) >= party.getMaxSize()) {
            throw new PartyFullException();
        }
        members.save(new PartyMember(UUID.randomUUID(), partyId, characterId));
        return view(party);
    }

    /** Leave the party. Promotes a new leader, or disbands the party if now empty. */
    @Transactional
    public Optional<PartyView> leave(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        PartyMember membership = members.findByCharacterId(characterId).orElseThrow(NotInPartyException::new);
        Party party = parties.findById(membership.getPartyId()).orElseThrow(PartyNotFoundException::new);

        members.delete(membership);
        List<PartyMember> remaining = members.findByPartyIdOrderByJoinedAtAsc(party.getId());
        if (remaining.isEmpty()) {
            party.disband();
            parties.save(party);
            return Optional.empty();
        }
        if (party.getLeaderCharacterId().equals(characterId)) {
            party.promoteLeader(remaining.getFirst().getCharacterId());
            parties.save(party);
        }
        return Optional.of(view(party));
    }

    /** Disband the whole party (leader only). */
    @Transactional
    public void disband(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        PartyMember membership = members.findByCharacterId(characterId).orElseThrow(NotInPartyException::new);
        Party party = parties.findById(membership.getPartyId()).orElseThrow(PartyNotFoundException::new);
        if (!party.getLeaderCharacterId().equals(characterId)) {
            throw new NotPartyLeaderException();
        }
        members.deleteAll(members.findByPartyIdOrderByJoinedAtAsc(party.getId()));
        party.disband();
        parties.save(party);
    }

    @Transactional(readOnly = true)
    public PartyView myParty(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        PartyMember membership = members.findByCharacterId(characterId).orElseThrow(NotInPartyException::new);
        Party party = parties.findById(membership.getPartyId()).orElseThrow(PartyNotFoundException::new);
        return view(party);
    }

    /** The id of the party this character belongs to, if any. */
    @Transactional(readOnly = true)
    public Optional<UUID> partyIdOf(UUID characterId) {
        return members.findByCharacterId(characterId).map(PartyMember::getPartyId);
    }

    /** The character ids of everyone currently in the party. */
    @Transactional(readOnly = true)
    public List<UUID> memberCharacterIds(UUID partyId) {
        return members.findByPartyIdOrderByJoinedAtAsc(partyId).stream()
                .map(PartyMember::getCharacterId)
                .toList();
    }

    /** True if the character is the leader of the given party. */
    @Transactional(readOnly = true)
    public boolean isLeader(UUID partyId, UUID characterId) {
        return parties.findById(partyId).map(p -> p.getLeaderCharacterId().equals(characterId)).orElse(false);
    }

    private void requireNotInParty(UUID characterId) {
        if (members.findByCharacterId(characterId).isPresent()) {
            throw new AlreadyInPartyException();
        }
    }

    private PartyView view(Party party) {
        List<PartyMember> roster = members.findByPartyIdOrderByJoinedAtAsc(party.getId());
        Map<UUID, String> names = characters.byIds(roster.stream().map(PartyMember::getCharacterId).toList())
                .stream().collect(Collectors.toMap(PlayerCharacter::getId, PlayerCharacter::getName));
        List<MemberView> memberViews = roster.stream()
                .map(m -> new MemberView(m.getCharacterId(),
                        names.getOrDefault(m.getCharacterId(), "?"),
                        m.getCharacterId().equals(party.getLeaderCharacterId())))
                .toList();
        return new PartyView(party.getId(), party.getLeaderCharacterId(),
                party.getStatus().name(), party.getMaxSize(), memberViews);
    }
}
