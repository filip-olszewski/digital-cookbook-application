package io.github.filipolszewski.cookbook.dto.recipe;

import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecipeCreateRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull Integer prepTime,
    @NotNull Integer servings,
    String imgUrl,
    @NotNull Long categoryId,
    List<Long> tagIds,
    @NotEmpty List<@Valid RecipeIngredientAddRequest> recipeIngredients,
    @NotEmpty List<@Valid StepAppendRequest> steps
) {}
