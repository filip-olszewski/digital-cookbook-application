package io.github.filipolszewski.cookbook.dto.category;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record CategorySummaryResponse(
    @NotNull Long id,
    @NotNull @Nullable Long parentId,
    @NotNull String name,
    @NotNull String slug
) {
}
