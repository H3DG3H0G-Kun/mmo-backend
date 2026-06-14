package ge.mmo.world.siege;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.clan.ClanService;
import ge.mmo.world.siege.SiegeViews.ParticipantView;
import ge.mmo.world.siege.SiegeViews.PlaceView;
import ge.mmo.world.siege.SiegeViews.SiegeView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Sieges: clans contest places of power. v1 covers the full lifecycle — a clan leader DECLAREs a
 * siege, other clans register, members CONTRIBUTE score (combat will feed this later), and the
 * declaring leader RESOLVEs it; the highest-scoring clan captures the place. Backend-authoritative.
 */
@Service
public class SiegeService {

    private final PlaceOfPowerRepository places;
    private final SiegeRepository sieges;
    private final SiegeParticipantRepository participants;
    private final CharacterService characters;
    private final ClanService clans;

    public SiegeService(PlaceOfPowerRepository places, SiegeRepository sieges,
                        SiegeParticipantRepository participants, CharacterService characters,
                        ClanService clans) {
        this.places = places;
        this.sieges = sieges;
        this.participants = participants;
        this.characters = characters;
        this.clans = clans;
    }

    @Transactional(readOnly = true)
    public List<PlaceView> listPlaces() {
        return places.findAllByOrderByEraIdAscNameAsc().stream().map(PlaceView::of).toList();
    }

    @Transactional(readOnly = true)
    public SiegeView getSiege(UUID siegeId) {
        return view(sieges.findById(siegeId).orElseThrow(SiegeNotFoundException::new));
    }

    /** A clan leader declares a siege on a place; their clan is registered to contest. */
    @Transactional
    public SiegeView declare(UUID accountId, UUID characterId, String placeCode) {
        UUID clanId = requireLeaderClan(accountId, characterId);
        PlaceOfPower place = places.findByCode(placeCode).orElseThrow(() -> new PlaceNotFoundException(placeCode));
        if (sieges.findByPlaceIdAndStatusNot(place.getId(), Siege.Status.RESOLVED).isPresent()) {
            throw new SiegeAlreadyOpenException();
        }
        Siege siege = sieges.save(new Siege(UUID.randomUUID(), place.getId(), clanId));
        participants.save(new SiegeParticipant(UUID.randomUUID(), siege.getId(), clanId));
        return view(siege);
    }

    /** Another clan's leader registers to contest a declared/active siege. */
    @Transactional
    public SiegeView join(UUID accountId, UUID characterId, UUID siegeId) {
        UUID clanId = requireLeaderClan(accountId, characterId);
        Siege siege = sieges.findById(siegeId).orElseThrow(SiegeNotFoundException::new);
        if (siege.getStatus() == Siege.Status.RESOLVED) {
            throw new SiegeStateException("Siege is already resolved");
        }
        if (participants.findBySiegeIdAndClanId(siegeId, clanId).isEmpty()) {
            participants.save(new SiegeParticipant(UUID.randomUUID(), siegeId, clanId));
        }
        return view(siege);
    }

    /** The declaring clan's leader starts the siege (DECLARED -> ACTIVE). */
    @Transactional
    public SiegeView start(UUID accountId, UUID characterId, UUID siegeId) {
        UUID clanId = requireLeaderClan(accountId, characterId);
        Siege siege = sieges.findById(siegeId).orElseThrow(SiegeNotFoundException::new);
        if (!siege.getDeclaringClanId().equals(clanId)) {
            throw new SiegeStateException("Only the declaring clan may start this siege");
        }
        if (siege.getStatus() != Siege.Status.DECLARED) {
            throw new SiegeStateException("Siege is not in DECLARED state");
        }
        siege.activate();
        return view(siege);
    }

    /** Any member of a participating clan contributes score (stands in for combat for now). */
    @Transactional
    public SiegeView contribute(UUID accountId, UUID characterId, UUID siegeId, long points) {
        characters.requireOwned(accountId, characterId);
        UUID clanId = clans.clanIdOf(characterId).orElseThrow(ClanRequiredException::new);
        Siege siege = sieges.findById(siegeId).orElseThrow(SiegeNotFoundException::new);
        if (siege.getStatus() != Siege.Status.ACTIVE) {
            throw new SiegeStateException("Siege is not active");
        }
        SiegeParticipant participant = participants.findBySiegeIdAndClanId(siegeId, clanId)
                .orElseThrow(NotASiegeParticipantException::new);
        participant.addScore(Math.max(1, points));
        return view(siege);
    }

    /** The declaring leader resolves the siege; the top-scoring clan captures the place. */
    @Transactional
    public SiegeView resolve(UUID accountId, UUID characterId, UUID siegeId) {
        UUID clanId = requireLeaderClan(accountId, characterId);
        Siege siege = sieges.findById(siegeId).orElseThrow(SiegeNotFoundException::new);
        if (!siege.getDeclaringClanId().equals(clanId)) {
            throw new SiegeStateException("Only the declaring clan may resolve this siege");
        }
        if (siege.getStatus() != Siege.Status.ACTIVE) {
            throw new SiegeStateException("Siege is not active");
        }
        List<SiegeParticipant> ranked = participants.findBySiegeIdOrderByScoreDesc(siegeId);
        UUID winner = ranked.isEmpty() ? null : ranked.getFirst().getClanId();
        siege.resolve(winner);
        if (winner != null) {
            places.findById(siege.getPlaceId()).ifPresent(p -> p.captureBy(winner));
        }
        return view(siege);
    }

    private UUID requireLeaderClan(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId);
        UUID clanId = clans.clanIdOf(characterId).orElseThrow(ClanRequiredException::new);
        if (!clans.isClanLeader(characterId)) {
            throw new NotClanLeaderException();
        }
        return clanId;
    }

    private SiegeView view(Siege siege) {
        List<ParticipantView> ps = participants.findBySiegeIdOrderByScoreDesc(siege.getId()).stream()
                .map(p -> new ParticipantView(p.getClanId(), p.getScore()))
                .toList();
        return new SiegeView(siege.getId(), siege.getPlaceId(), siege.getStatus().name(),
                siege.getDeclaringClanId(), siege.getWinnerClanId(), ps);
    }
}
