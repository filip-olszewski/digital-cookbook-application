package io.github.filipolszewski.cookbook.dto.recipe;

import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecipeUpdateRequest(
    String name,
    String description,
    Integer prepTime,
    Integer servings,
    Long categoryId,
    List<Long> tagIds,
    List<@Valid RecipeIngredientAddRequest> recipeIngredients,
    List<@Valid StepAppendRequest> steps
) {}
