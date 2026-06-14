package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateClanRequest(
        @NotNull UUID characterId,
        @NotBlank @Size(min = 3, max = 32) String name,
        @NotBlank @Size(min = 2, max = 5) String tag,
        Integer maxMembers) {
}
