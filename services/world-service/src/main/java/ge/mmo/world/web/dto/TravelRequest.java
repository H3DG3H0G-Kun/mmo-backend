package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Intent to travel a character to another era (backend validates it is unlocked). */
public record TravelRequest(
        @NotNull UUID characterId,
        @NotBlank String eraCode) {
}
