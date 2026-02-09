package io.github.filipolszewski.cookbook.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

public record ReviewUpdateRequest(
    Integer rating,
    JsonNullable<String> comment
) {}
