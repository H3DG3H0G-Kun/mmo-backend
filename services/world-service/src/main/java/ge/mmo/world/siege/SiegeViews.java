package ge.mmo.world.siege;

import java.util.List;
import java.util.UUID;

public final class SiegeViews {
    private SiegeViews() {
    }

    public record PlaceView(UUID id, String code, String name, Integer eraId, UUID holderClanId) {
        static PlaceView of(PlaceOfPower p) {
            return new PlaceView(p.getId(), p.getCode(), p.getName(), p.getEraId(), p.getHolderClanId());
        }
    }

    public record ParticipantView(UUID clanId, long score) {
    }

    public record SiegeView(
            UUID id,
            UUID placeId,
            String status,
            UUID declaringClanId,
            UUID winnerClanId,
            List<ParticipantView> participants) {
    }
}
