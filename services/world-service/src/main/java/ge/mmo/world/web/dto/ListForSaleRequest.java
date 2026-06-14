package ge.mmo.world.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ListForSaleRequest(
        @NotNull UUID characterId,
        @NotBlank String itemCode,
        @Min(1) long quantity,
        @Min(1) long unitPrice) {
}
