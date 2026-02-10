package io.github.filipolszewski.cookbook.dto.user;

public record UserSummaryResponse(
    Long id,
    String username,
    String fullName
) {}