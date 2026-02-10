package io.github.filipolszewski.cookbook.dto.recipe;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecipeSummaryResponse(
    Long id,
    String name,
    String slug,
    Integer prepTime,
    String imgUrl,
    Double rating,
    Integer reviewCount,
    Integer favouriteCount,
    String authorName,
    String category,
    List<String> tags
) {}
