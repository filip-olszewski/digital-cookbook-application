package io.github.filipolszewski.cookbook.specification.criteria;

public record CategorySearchCriteria(
    String name,
    Long parentId,
    Boolean root
) {}
