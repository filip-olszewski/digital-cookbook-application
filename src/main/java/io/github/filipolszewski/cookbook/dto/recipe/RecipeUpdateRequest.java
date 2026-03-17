package io.github.filipolszewski.cookbook.dto.recipe;

import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

public record RecipeUpdateRequest(
    String name,
    String description,
    Integer prepTime,
    Integer servings,
    JsonNullable<String> imageKey,
    Long categoryId,
    List<Long> tagIds,
    List<@Valid RecipeIngredientAddRequest> recipeIngredients,
    List<@Valid StepAppendRequest> steps
) {}
