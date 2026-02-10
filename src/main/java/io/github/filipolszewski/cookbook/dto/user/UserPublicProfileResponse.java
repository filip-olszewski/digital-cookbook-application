package io.github.filipolszewski.cookbook.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserPublicProfileResponse(
    Long id,
    String username,
    String fullName,
    String avatarUrl,
    String bio,
    Instant joinedAt,
    long recipeCount
) {}
