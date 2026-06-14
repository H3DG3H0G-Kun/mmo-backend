package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** An action one character takes on another within a clan (invite, kick). */
public record ClanMemberActionRequest(
        @NotNull UUID characterId,
        @NotNull UUID targetCharacterId) {
}
