package io.github.filipolszewski.cookbook.dto.category;

public record CategoryDetailsResponse(
    Long id,
    String name,
    String slug,
    String imgUrl,
    CategorySummaryResponse parent
) {
}
