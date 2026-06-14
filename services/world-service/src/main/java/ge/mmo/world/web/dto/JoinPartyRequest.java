package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JoinPartyRequest(
        @NotNull UUID characterId) {
}
