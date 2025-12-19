package io.github.filipolszewski.cookbook.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewPostRequest(
    @NotNull Long recipeId,
    @NotNull Integer rating,
    @NotBlank String comment
) {}
