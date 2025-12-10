package io.github.filipolszewski.cookbook.dto.recipeingredient;

import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;

public record RecipeIngredientResponse(
    Long id,
    String name,
    IngredientType type,
    Double amount,
    String unit
) {}
