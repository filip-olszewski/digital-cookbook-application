package io.github.filipolszewski.cookbook.specification;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<E, C> {
    Specification<E> build(C criteria);
}
