package io.github.filipolszewski.cookbook.specification.criteria;

import java.util.List;

public record RecipeSearchCriteria(
    String name,
    Integer maxPrepTime,
    Double minRating,
    List<String> tags,
    List<String> categories
) {}
