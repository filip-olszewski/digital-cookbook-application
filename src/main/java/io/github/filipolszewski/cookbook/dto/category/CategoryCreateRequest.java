package io.github.filipolszewski.cookbook.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
    @NotBlank String name,
    String imageKey,
    Long parentId
) {}
