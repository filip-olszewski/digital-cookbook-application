package io.github.filipolszewski.cookbook.dto.review;

import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReviewResponse(
    Long id,
    Integer rating,
    String comment,
    Instant postedAt,
    UserSummaryResponse user
) {
}
