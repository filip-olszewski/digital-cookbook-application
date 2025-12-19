package io.github.filipolszewski.cookbook.dto.review;

import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReviewResponse(
    @NotNull Long id,
    @NotNull Integer rating,
    @Nullable String comment,
    @NotNull Instant postedAt,
    @NotNull UserSummaryResponse user
) {
}
