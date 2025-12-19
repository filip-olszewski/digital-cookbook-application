package io.github.filipolszewski.cookbook.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record UserSummaryResponse(
    @NotNull Long id,
    @NotNull String email,
    @NotNull String username,
    @NotNull String firstName,
    @NotNull @Nullable String middleName,
    @NotNull String lastName
) {}