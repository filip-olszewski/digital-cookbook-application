package io.github.filipolszewski.cookbook.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(
    Integer rating,
    String comment
) {}
