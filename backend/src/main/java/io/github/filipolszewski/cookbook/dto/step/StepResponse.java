package io.github.filipolszewski.cookbook.dto.step;

import jakarta.validation.constraints.NotNull;

public record StepResponse(
    @NotNull Long id,
    @NotNull Integer stepOrder,
    @NotNull String instructions,
    @NotNull String imgUrl
) {}
