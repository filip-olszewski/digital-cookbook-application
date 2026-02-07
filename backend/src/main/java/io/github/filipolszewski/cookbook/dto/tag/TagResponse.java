package io.github.filipolszewski.cookbook.dto.tag;

import jakarta.validation.constraints.NotNull;

public record TagResponse(
    @NotNull Long id,
    @NotNull String label
) {}
