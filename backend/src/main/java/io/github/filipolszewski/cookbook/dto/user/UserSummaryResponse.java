package io.github.filipolszewski.cookbook.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserSummaryResponse(
    @NotNull Long id,
    @NotNull String username,
    @NotNull String fullName
) {}