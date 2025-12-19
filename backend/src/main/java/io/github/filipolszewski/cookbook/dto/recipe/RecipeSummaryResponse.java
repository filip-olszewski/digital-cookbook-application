package io.github.filipolszewski.cookbook.dto.recipe;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecipeSummaryResponse(
    @NotNull Long id,
    @NotNull String name,
    @NotNull String slug,
    @NotNull Integer prepTime,
    @Nullable String imgUrl,
    @NotNull Double rating,
    @NotNull Integer reviewCount,
    @NotNull String authorName,
    @NotNull String category,
    @NotNull List<String> tags
) {}
