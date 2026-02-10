package io.github.filipolszewski.cookbook.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserPrivateProfileResponse(
    Long id,
    String username,
    String email,
    String fullName,
    String avatarUrl,
    String bio,
    Instant joinedAt,
    long recipeCount,
    long reviewCount,
    long favouriteCount
) {
}
