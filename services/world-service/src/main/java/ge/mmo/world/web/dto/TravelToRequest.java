package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Intent to travel within the current era to a connected location. */
public record TravelToRequest(
        @NotNull UUID characterId,
        @NotBlank String locationCode) {
}
