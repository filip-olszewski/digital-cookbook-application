package io.github.filipolszewski.cookbook.specification.criteria;

import java.util.List;

public record RecipeSearchCriteria(
    Integer maxPrepTime,
    Double minRating,
    List<Long> tags,
    List<Long> categories
) {}
