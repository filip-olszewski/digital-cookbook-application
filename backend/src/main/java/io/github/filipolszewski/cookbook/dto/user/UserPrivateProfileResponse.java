package io.github.filipolszewski.cookbook.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserPrivateProfileResponse(
    @NotNull Long id,
    @NotNull String username,
    @NotNull String email,
    @NotNull String fullName,
    @Nullable String avatarUrl,
    @Nullable String bio,
    @NotNull Instant joinedAt,
    @NotNull long recipeCount,
    @NotNull long reviewCount,
    @NotNull long favouriteCount
) {
}
