package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Intent to advance a Tale; choiceKey is required only at CHOOSE beats. */
public record AdvanceTaleRequest(
        @NotNull UUID characterId,
        String choiceKey) {
}
