package io.github.filipolszewski.cookbook.dto.recipeingredient;

import jakarta.validation.constraints.NotNull;

public record RecipeIngredientAddRequest(
    @NotNull Integer ingredientId,
    @NotNull Double amount,
    @NotNull String unit
) {}
