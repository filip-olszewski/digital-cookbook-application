package io.github.filipolszewski.cookbook.dto.category;

import jakarta.validation.constraints.NotNull;

public record CategoryDetailsResponse(
    @NotNull Long id,
    @NotNull String name,
    @NotNull String slug,
    @NotNull String imgUrl,
    @NotNull CategorySummaryResponse parent
) {
}
