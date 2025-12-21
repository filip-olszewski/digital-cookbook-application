package io.github.filipolszewski.cookbook.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank String email,
        @NotBlank String username,
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @NotBlank String password
) {}