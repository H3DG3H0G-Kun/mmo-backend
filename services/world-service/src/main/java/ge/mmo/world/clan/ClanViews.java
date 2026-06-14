package ge.mmo.world.clan;

import java.util.List;
import java.util.UUID;

public final class ClanViews {
    private ClanViews() {
    }

    public record ClanMemberView(UUID characterId, String name, ClanRank rank) {
    }

    public record ClanView(
            UUID id,
            String name,
            String tag,
            UUID leaderCharacterId,
            int maxMembers,
            List<ClanMemberView> members) {
    }
}
