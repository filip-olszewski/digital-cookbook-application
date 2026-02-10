package io.github.filipolszewski.cookbook.dto.category;

import jakarta.validation.constraints.NotNull;

public record CategorySummaryResponse(
    Long id,
    String name,
    String slug,
    Long parentId
) {
}
