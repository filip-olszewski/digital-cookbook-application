package io.github.filipolszewski.cookbook.dto.ingredient;

import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;

public record IngredientUpdateRequest(
    String name,
    IngredientType type
) {}
