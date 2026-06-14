package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** A character-scoped party action (create with optional maxSize, leave, disband, query). */
public record PartyActionRequest(
        @NotNull UUID characterId,
        Integer maxSize) {
}
