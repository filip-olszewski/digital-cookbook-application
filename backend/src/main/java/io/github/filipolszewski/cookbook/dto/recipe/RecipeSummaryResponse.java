package io.github.filipolszewski.cookbook.dto.recipe;

import java.util.List;

public record RecipeSummaryResponse(
    Long id,
    String name,
    String slug,
    Integer prepTime,
    String imgUrl,
    Double rating,
    String authorName,
    String category,
    List<String> tags
) {}
