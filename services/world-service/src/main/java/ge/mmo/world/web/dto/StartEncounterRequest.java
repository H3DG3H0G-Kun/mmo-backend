package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartEncounterRequest(
        @NotNull UUID characterId,
        @NotBlank String enemyCode,
        UUID siegeId) {
}
