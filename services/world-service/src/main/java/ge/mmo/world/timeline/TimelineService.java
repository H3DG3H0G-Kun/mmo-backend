package ge.mmo.world.timeline;

import ge.mmo.world.character.CharacterService;
import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.timeline.TimelineViews.EraStatusView;
import ge.mmo.world.timeline.TimelineViews.TimelineEraView;
import ge.mmo.world.world.Era;
import ge.mmo.world.world.EraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The timeline layer: which eras a Watcher may travel to (personal unlocks) and the server-wide
 * Living Timeline. Era travel is backend-validated — a locked era is unreachable.
 */
@Service
public class TimelineService {

    private final EraUnlockRepository unlocks;
    private final TimelineProgressRepository progress;
    private final EraRepository eras;
    private final CharacterService characters;

    public TimelineService(EraUnlockRepository unlocks, TimelineProgressRepository progress,
                           EraRepository eras, CharacterService characters) {
        this.unlocks = unlocks;
        this.progress = progress;
        this.eras = eras;
        this.characters = characters;
    }

    /** Grant a character access to an era (idempotent). Default-unlocked eras need no row. */
    @Transactional
    public void unlockEra(UUID characterId, Integer eraId) {
        if (!unlocks.existsByCharacterIdAndEraId(characterId, eraId)) {
            unlocks.save(new EraUnlock(UUID.randomUUID(), characterId, eraId));
        }
    }

    /** Increment the server-wide restored count for an era (a memory was restored). */
    @Transactional
    public void recordRestoration(Integer eraId) {
        progress.findById(eraId).ifPresent(p -> {
            p.incrementRestored();
            progress.save(p);
        });
    }

    @Transactional(readOnly = true)
    public boolean isUnlocked(UUID characterId, Era era) {
        return era.isDefaultUnlocked() || unlocks.existsByCharacterIdAndEraId(characterId, era.getId());
    }

    /** All eras in timeline order with this character's unlock status. */
    @Transactional(readOnly = true)
    public List<EraStatusView> listEras(UUID accountId, UUID characterId) {
        characters.requireOwned(accountId, characterId); // ownership check
        Set<Integer> unlockedIds = unlocks.findByCharacterId(characterId).stream()
                .map(EraUnlock::getEraId)
                .collect(Collectors.toSet());
        return eras.findAllByOrderByOrdinalAsc().stream()
                .map(e -> EraStatusView.of(e, e.isDefaultUnlocked() || unlockedIds.contains(e.getId())))
                .toList();
    }

    /** Travel a character to an era, if unlocked. The backend decides; the client only intends. */
    @Transactional
    public PlayerCharacter travel(UUID accountId, UUID characterId, String eraCode) {
        PlayerCharacter character = characters.requireOwned(accountId, characterId);
        Era era = eras.findByCode(eraCode).orElseThrow(() -> new EraNotFoundException(eraCode));
        if (!isUnlocked(characterId, era)) {
            throw new EraLockedException(eraCode);
        }
        return characters.relocate(character, era);
    }

    /** The server-wide Living Timeline: memories restored per era. */
    @Transactional(readOnly = true)
    public List<TimelineEraView> livingTimeline() {
        return eras.findAllByOrderByOrdinalAsc().stream()
                .map(e -> new TimelineEraView(e.getId(), e.getCode(), e.getName(), e.getOrdinal(),
                        progress.findById(e.getId()).map(TimelineProgress::getRestoredCount).orElse(0L)))
                .toList();
    }
}
