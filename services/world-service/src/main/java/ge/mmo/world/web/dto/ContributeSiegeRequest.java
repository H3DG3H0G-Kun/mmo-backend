package ge.mmo.world.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ContributeSiegeRequest(
        @NotNull UUID characterId,
        @Min(1) long points) {
}
