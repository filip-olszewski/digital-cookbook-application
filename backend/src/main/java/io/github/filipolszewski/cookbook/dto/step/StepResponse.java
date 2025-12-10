package io.github.filipolszewski.cookbook.dto.step;

public record StepResponse(
    Long id,
    Integer stepOrder,
    String instructions,
    String imgUrl
) {}
