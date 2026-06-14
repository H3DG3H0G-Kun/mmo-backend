package ge.mmo.world.party;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PartyServiceTest {

    private PartyRepository parties;
    private PartyMemberRepository members;
    private CharacterService characters;
    private PartyService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        parties = mock(PartyRepository.class);
        members = mock(PartyMemberRepository.class);
        characters = mock(CharacterService.class);
        service = new PartyService(parties, members, characters);
        when(characters.requireOwned(any(), any())).thenReturn(mock(PlayerCharacter.class));
        when(characters.byIds(any())).thenReturn(List.of());
    }

    @Test
    void createWhenAlreadyInPartyIsRejected() {
        when(members.findByCharacterId(charId))
                .thenReturn(Optional.of(new PartyMember(UUID.randomUUID(), UUID.randomUUID(), charId)));

        assertThatThrownBy(() -> service.create(acc, charId, 5))
                .isInstanceOf(AlreadyInPartyException.class);
    }

    @Test
    void joinFullPartyIsRejected() {
        UUID partyId = UUID.randomUUID();
        when(members.findByCharacterId(charId)).thenReturn(Optional.empty());
        Party party = new Party(partyId, UUID.randomUUID(), 2);
        when(parties.findById(partyId)).thenReturn(Optional.of(party));
        when(members.countByPartyId(partyId)).thenReturn(2L);

        assertThatThrownBy(() -> service.join(acc, charId, partyId))
                .isInstanceOf(PartyFullException.class);
    }

    @Test
    void leaveAsLastMemberDisbandsParty() {
        UUID partyId = UUID.randomUUID();
        Party party = new Party(partyId, charId, 5);
        PartyMember membership = new PartyMember(UUID.randomUUID(), partyId, charId);
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(membership));
        when(parties.findById(partyId)).thenReturn(Optional.of(party));
        when(members.findByPartyIdOrderByJoinedAtAsc(partyId)).thenReturn(List.of());

        Optional<?> result = service.leave(acc, charId);

        assertThat(result).isEmpty();
        assertThat(party.getStatus()).isEqualTo(Party.Status.DISBANDED);
        verify(members).delete(membership);
    }

    @Test
    void leavePromotesNewLeaderWhenMembersRemain() {
        UUID partyId = UUID.randomUUID();
        UUID other = UUID.randomUUID();
        Party party = new Party(partyId, charId, 5); // charId is leader
        PartyMember leaving = new PartyMember(UUID.randomUUID(), partyId, charId);
        PartyMember remaining = new PartyMember(UUID.randomUUID(), partyId, other);
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(leaving));
        when(parties.findById(partyId)).thenReturn(Optional.of(party));
        when(members.findByPartyIdOrderByJoinedAtAsc(partyId)).thenReturn(List.of(remaining));

        service.leave(acc, charId);

        assertThat(party.getLeaderCharacterId()).isEqualTo(other);
    }

    @Test
    void disbandByNonLeaderIsRejected() {
        UUID partyId = UUID.randomUUID();
        Party party = new Party(partyId, UUID.randomUUID(), 5); // someone else is leader
        when(members.findByCharacterId(charId))
                .thenReturn(Optional.of(new PartyMember(UUID.randomUUID(), partyId, charId)));
        when(parties.findById(partyId)).thenReturn(Optional.of(party));

        assertThatThrownBy(() -> service.disband(acc, charId))
                .isInstanceOf(NotPartyLeaderException.class);
    }
}
