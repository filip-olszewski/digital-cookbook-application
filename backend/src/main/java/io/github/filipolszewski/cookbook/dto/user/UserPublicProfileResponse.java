package io.github.filipolszewski.cookbook.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserPublicProfileResponse(
    @NotNull Long id,
    @NotNull String username,
    @NotNull String fullName,
    @Nullable String avatarUrl,
    @Nullable String bio,
    @NotNull Instant joinedAt,
    @NotNull long recipeCount
) {}
