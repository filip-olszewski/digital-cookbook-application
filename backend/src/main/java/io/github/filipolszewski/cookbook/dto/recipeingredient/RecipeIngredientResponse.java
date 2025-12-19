package io.github.filipolszewski.cookbook.dto.recipeingredient;

import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import jakarta.validation.constraints.NotNull;

public record RecipeIngredientResponse(
    @NotNull Long id,
    @NotNull String name,
    @NotNull IngredientType type,
    @NotNull Double amount,
    @NotNull String unit
) {}
