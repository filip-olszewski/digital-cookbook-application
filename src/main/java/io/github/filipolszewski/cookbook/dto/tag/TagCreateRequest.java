package io.github.filipolszewski.cookbook.dto.tag;

import jakarta.validation.constraints.NotBlank;

public record TagCreateRequest(
    @NotBlank String label
) {}
