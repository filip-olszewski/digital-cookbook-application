package io.github.filipolszewski.cookbook.dto.category;

import org.openapitools.jackson.nullable.JsonNullable;

public record CategoryUpdateRequest(
    String name,
    JsonNullable<String> imgUrl,
    JsonNullable<Long> parentId
) {}
