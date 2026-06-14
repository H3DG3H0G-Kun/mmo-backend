package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** A siege action by a character (join, start, resolve); siege id comes from the path. */
public record SiegeActorRequest(
        @NotNull UUID characterId) {
}
