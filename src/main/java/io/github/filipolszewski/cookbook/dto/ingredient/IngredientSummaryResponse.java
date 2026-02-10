package io.github.filipolszewski.cookbook.dto.ingredient;

import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import jakarta.validation.constraints.NotNull;

public record IngredientSummaryResponse(
    Long id,
    String name,
    IngredientType type
) {
}
