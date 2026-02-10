package io.github.filipolszewski.cookbook.dto.step;

import jakarta.validation.constraints.NotNull;

public record StepResponse(
    Long id,
    Integer stepOrder,
    String instructions,
    String imgUrl
) {}
