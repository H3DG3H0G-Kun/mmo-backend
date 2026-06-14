package ge.mmo.world.party;

import java.util.List;
import java.util.UUID;

public final class PartyViews {
    private PartyViews() {
    }

    public record MemberView(UUID characterId, String name, boolean leader) {
    }

    public record PartyView(
            UUID id,
            UUID leaderCharacterId,
            String status,
            int maxSize,
            List<MemberView> members) {
    }
}
