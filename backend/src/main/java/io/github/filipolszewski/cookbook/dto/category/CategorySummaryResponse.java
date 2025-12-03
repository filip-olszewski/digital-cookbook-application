package io.github.filipolszewski.cookbook.dto.category;

public record CategorySummaryResponse(
    Long id,
    String name,
    String slug
) {
}
