package io.github.filipolszewski.cookbook.dto.review;

import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;

public record ReviewResponse(
    Long id,
    Integer rating,
    String comment,
    UserSummaryResponse user
) {
}
