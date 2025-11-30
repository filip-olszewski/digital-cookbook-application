package io.github.filipolszewski.cookbook.dto.user;

public record UserSummaryResponse(
        Long id,
        String email,
        String firstName,
        String middleName,
        String lastName
) {}