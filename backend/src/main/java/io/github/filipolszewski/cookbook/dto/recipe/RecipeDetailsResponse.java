package io.github.filipolszewski.cookbook.dto.recipe;

import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.step.StepResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public record RecipeDetailsResponse(
    Long id,
    String name,
    String slug,
    String description,
    Integer prepTime,
    Integer servings,
    LocalDate publicationDate,
    String imgUrl,
    Double rating,
    Integer favouriteCount,
    CategorySummaryResponse category,
    UserSummaryResponse author,
    List<TagResponse> tags,
    List<RecipeIngredientResponse> ingredients,
    List<ReviewResponse> reviews,
    List<StepResponse> steps
) {}
