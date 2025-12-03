package io.github.filipolszewski.cookbook.dto.recipe;

import java.time.LocalDate;
import java.util.List;

public record RecipeDetailsResponse(
    Long id,
    String name,
    String slug,
    Integer prepTime,
    LocalDate publicationDate,
    String imgUrl,
    Double rating
) {}
