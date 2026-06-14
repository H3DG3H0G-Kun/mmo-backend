package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** A clan action a character takes on themselves (accept invite, leave). */
public record ClanSelfRequest(
        @NotNull UUID characterId) {
}
