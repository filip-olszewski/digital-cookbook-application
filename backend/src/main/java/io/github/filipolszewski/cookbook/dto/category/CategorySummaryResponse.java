package io.github.filipolszewski.cookbook.dto.category;

public record CategorySummaryResponse(
    Long id,
    Long parentId,
    String name,
    String slug
) {
}
