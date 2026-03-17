package io.github.filipolszewski.cookbook.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewPostRequest(
    @NotNull @Min(1) @Max(5) Integer rating,
    String comment
) {}
