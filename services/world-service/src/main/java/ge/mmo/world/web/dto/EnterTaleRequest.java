package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

/** Intent to enter a Tale as a given character, from a place with active world states. */
public record EnterTaleRequest(
        @NotNull UUID characterId,
        String place,
        Set<String> states) {
}
