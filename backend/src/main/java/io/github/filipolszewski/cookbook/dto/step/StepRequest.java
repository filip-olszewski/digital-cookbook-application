package io.github.filipolszewski.cookbook.dto.step;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StepRequest(
    @NotNull Integer stepOrder,
    @NotBlank String instructions,
    String imgUrl
) {
}
