package io.github.filipolszewski.cookbook.dto.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
