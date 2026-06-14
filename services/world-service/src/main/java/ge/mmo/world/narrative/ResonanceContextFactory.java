package ge.mmo.world.narrative;

import ge.mmo.world.character.PlayerCharacter;
import ge.mmo.world.narrative.NarrativeEnums.Progress;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Builds the {@link ResonanceContext} for a character (era + place + states + completed Tales). */
@Component
public class ResonanceContextFactory {

    private final TaleProgressRepository progressRepo;
    private final TaleRepository tales;

    public ResonanceContextFactory(TaleProgressRepository progressRepo, TaleRepository tales) {
        this.progressRepo = progressRepo;
        this.tales = tales;
    }

    @Transactional(readOnly = true)
    public ResonanceContext forCharacter(PlayerCharacter character, String place, Set<String> states) {
        Set<UUID> completedIds = progressRepo.findByCharacterId(character.getId()).stream()
                .filter(p -> p.getStatus() == Progress.COMPLETED)
                .map(TaleProgress::getTaleId)
                .collect(Collectors.toSet());
        Set<String> completedCodes = tales.findAllById(completedIds).stream()
                .map(Tale::getCode)
                .collect(Collectors.toSet());
        return new ResonanceContext(character.getCurrentEra().getCode(), place, states, completedCodes);
    }
}
