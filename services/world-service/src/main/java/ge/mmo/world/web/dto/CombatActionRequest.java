package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** A combat action by a character (attack); the encounter id comes from the path. */
public record CombatActionRequest(
        @NotNull UUID characterId) {
}
