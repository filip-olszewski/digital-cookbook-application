package io.github.filipolszewski.cookbook.dto.recipeingredient;

import jakarta.validation.constraints.NotNull;

public record RecipeIngredientAddRequest(
    @NotNull Long ingredientId,
    @NotNull Double amount,
    @NotNull String unit
) {}
