package ge.mmo.world.clan;

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
import static org.mockito.Mockito.when;

class ClanServiceTest {

    private ClanRepository clans;
    private ClanMemberRepository members;
    private ClanInviteRepository invites;
    private CharacterService characters;
    private ClanService service;

    private final UUID acc = UUID.randomUUID();
    private final UUID charId = UUID.randomUUID();
    private final UUID clanId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        clans = mock(ClanRepository.class);
        members = mock(ClanMemberRepository.class);
        invites = mock(ClanInviteRepository.class);
        characters = mock(CharacterService.class);
        service = new ClanService(clans, members, invites, characters);
        when(characters.requireOwned(any(), any())).thenReturn(mock(PlayerCharacter.class));
        when(characters.byIds(any())).thenReturn(List.of());
    }

    private ClanMember member(UUID character, ClanRank rank) {
        return new ClanMember(UUID.randomUUID(), clanId, character, rank);
    }

    @Test
    void createWhenAlreadyInClanIsRejected() {
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(member(charId, ClanRank.MEMBER)));
        assertThatThrownBy(() -> service.create(acc, charId, "The Watch", "WTCH", null))
                .isInstanceOf(AlreadyInClanException.class);
    }

    @Test
    void inviteByPlainMemberIsRejected() {
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(member(charId, ClanRank.MEMBER)));
        assertThatThrownBy(() -> service.invite(acc, charId, UUID.randomUUID()))
                .isInstanceOf(InsufficientClanRankException.class);
    }

    @Test
    void kickingEqualRankIsRejected() {
        UUID targetId = UUID.randomUUID();
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(member(charId, ClanRank.OFFICER)));
        when(members.findByClanIdAndCharacterId(clanId, targetId))
                .thenReturn(Optional.of(member(targetId, ClanRank.OFFICER)));
        assertThatThrownBy(() -> service.kick(acc, charId, targetId))
                .isInstanceOf(InsufficientClanRankException.class);
    }

    @Test
    void leaveAsLeaderPromotesSuccessor() {
        UUID successorId = UUID.randomUUID();
        Clan clan = new Clan(clanId, "The Watch", "WTCH", charId, 30);
        ClanMember leaving = member(charId, ClanRank.LEADER);
        ClanMember successor = member(successorId, ClanRank.OFFICER);
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(leaving));
        when(clans.findById(clanId)).thenReturn(Optional.of(clan));
        when(members.findByClanIdOrderByJoinedAtAsc(clanId)).thenReturn(List.of(successor));

        service.leave(acc, charId);

        assertThat(successor.getRank()).isEqualTo(ClanRank.LEADER);
        assertThat(clan.getLeaderCharacterId()).isEqualTo(successorId);
    }

    @Test
    void setRankToLeaderTransfersLeadership() {
        UUID targetId = UUID.randomUUID();
        Clan clan = new Clan(clanId, "The Watch", "WTCH", charId, 30);
        ClanMember leader = member(charId, ClanRank.LEADER);
        ClanMember target = member(targetId, ClanRank.MEMBER);
        when(members.findByCharacterId(charId)).thenReturn(Optional.of(leader));
        when(members.findByClanIdAndCharacterId(clanId, targetId)).thenReturn(Optional.of(target));
        when(clans.findById(clanId)).thenReturn(Optional.of(clan));
        when(members.findByClanIdOrderByJoinedAtAsc(clanId)).thenReturn(List.of(leader, target));

        service.setRank(acc, charId, targetId, ClanRank.LEADER);

        assertThat(target.getRank()).isEqualTo(ClanRank.LEADER);
        assertThat(leader.getRank()).isEqualTo(ClanRank.OFFICER);
        assertThat(clan.getLeaderCharacterId()).isEqualTo(targetId);
    }
}
