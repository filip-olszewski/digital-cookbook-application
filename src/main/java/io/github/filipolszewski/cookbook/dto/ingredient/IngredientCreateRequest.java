package io.github.filipolszewski.cookbook.dto.ingredient;

import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngredientCreateRequest(
    @NotBlank String name,
    @NotNull IngredientType type
) {}
