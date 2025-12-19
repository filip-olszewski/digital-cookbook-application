package io.github.filipolszewski.cookbook.specification;

import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.specification.criteria.CategorySearchCriteria;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategorySpecificationBuilder implements SpecificationBuilder<Category, CategorySearchCriteria> {

    private Specification<Category> hasName(String name) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private Specification<Category> hasParentId(Long parentId) {
        return (root, query, cb) -> {
            return cb.equal(root.get("parentCategory").get("id"), parentId);
        };
    }

    private Specification<Category> isRoot(boolean isRoot) {
        return (root, query, cb) ->
            isRoot ?
                cb.isNull(root.get("parentCategory")) :
                cb.isNotNull(root.get("parentCategory"));
    }

    @Override
    public Specification<Category> build(CategorySearchCriteria criteria) {
        Specification<Category> spec = (root, query, cb) ->
            cb.conjunction();

        if(criteria.name() != null && !criteria.name().isBlank()) {
            spec = spec.and(hasName(criteria.name()));
        }

        if(criteria.root() != null) {
            spec = spec.and(isRoot(criteria.root()));
        }

        if(criteria.parentId() != null) {
            spec = spec.and(hasParentId(criteria.parentId()));
        }

        return spec;
    }
}
