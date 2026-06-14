package ge.mmo.world.web.dto;

import ge.mmo.world.character.PlayerCharacter;

import java.time.Instant;
import java.util.UUID;

public record CharacterResponse(
        UUID id,
        String name,
        EraView currentEra,
        Instant createdAt) {

    public static CharacterResponse of(PlayerCharacter c) {
        return new CharacterResponse(
                c.getId(),
                c.getName(),
                EraView.of(c.getCurrentEra()),
                c.getCreatedAt());
    }
}
