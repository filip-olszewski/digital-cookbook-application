package io.github.filipolszewski.cookbook.dto.recipe;

import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.step.StepResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RecipeDetailsResponse(
    @NotNull Long id,
    @NotNull String name,
    @NotNull String slug,
    @NotNull String description,
    @NotNull Integer prepTime,
    @NotNull Integer servings,
    @NotNull LocalDate publicationDate,
    @Nullable String imgUrl,
    @NotNull Double rating,
    @NotNull Integer reviewCount,
    @NotNull Integer favouriteCount,
    @NotNull CategorySummaryResponse category,
    @NotNull UserSummaryResponse author,
    @NotNull List<TagResponse> tags,
    @NotNull List<RecipeIngredientResponse> ingredients,
    @NotNull List<StepResponse> steps
) {}
