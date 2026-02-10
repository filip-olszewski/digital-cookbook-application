package io.github.filipolszewski.cookbook.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank String token
) {}
