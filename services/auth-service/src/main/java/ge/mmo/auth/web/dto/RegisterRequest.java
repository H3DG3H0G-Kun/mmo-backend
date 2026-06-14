package ge.mmo.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 32)
        @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "must be letters, digits or underscore")
        String username,

        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password) {
}
