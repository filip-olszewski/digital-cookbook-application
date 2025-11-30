package io.github.filipolszewski.cookbook.dto.ingredient;

import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;

public record IngredientSummaryResponse(
    Long id,
    String name,
    IngredientType type
) {
}
