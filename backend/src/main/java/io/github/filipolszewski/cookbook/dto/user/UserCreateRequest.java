package io.github.filipolszewski.cookbook.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank String email,
        @NotBlank String firstName,
        String middleName,
        @NotBlank String lastName,
        @NotBlank String password
) {}
