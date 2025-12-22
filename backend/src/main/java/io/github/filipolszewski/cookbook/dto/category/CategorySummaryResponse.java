package io.github.filipolszewski.cookbook.dto.category;

import jakarta.validation.constraints.NotNull;

public record CategorySummaryResponse(
    @NotNull Long id,
    @NotNull String name,
    @NotNull String slug,
    Long parentId
) {
}
