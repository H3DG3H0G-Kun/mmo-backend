package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** A character-scoped economy action (forage, buy, cancel); ids beyond the character come from the path. */
public record ShopActionRequest(
        @NotNull UUID characterId) {
}
