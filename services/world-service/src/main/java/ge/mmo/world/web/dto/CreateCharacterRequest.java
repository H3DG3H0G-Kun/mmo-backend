package ge.mmo.world.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCharacterRequest(
        @NotBlank
        @Size(min = 3, max = 24)
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "must be letters, digits or underscore")
        String name) {
}
