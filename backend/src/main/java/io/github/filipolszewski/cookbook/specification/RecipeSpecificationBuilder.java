package io.github.filipolszewski.cookbook.specification;

import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RecipeSpecificationBuilder implements SpecificationBuilder<Recipe, RecipeSearchCriteria> {

    private Specification<Recipe> hasName(String name) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    private Specification<Recipe> hasAuthor(String author) {
        return (root, query, cb) -> {
            return cb.equal(root.get("author").get("username"), author);
        };
    }

    private Specification<Recipe> hasPrepTime(Integer maxPrepTime) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("prepTime"), maxPrepTime);
    }

    private Specification<Recipe> hasRating(Double minRating) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("averageRating"), minRating);
    }

    private Specification<Recipe> containsTag(List<String> tagSlugs) {
        return (root, query, cb) -> {
            Join<Tag, Recipe> tags = root.join("tags", JoinType.LEFT);
            return tags.get("slug").in(tagSlugs);
        };
    }

    private Specification<Recipe> containsCategory(List<String> categorySlugs) {
        return (root, query, cb) ->
            root.get("category").get("slug").in(categorySlugs);
    }

    @Override
    public Specification<Recipe> build(RecipeSearchCriteria criteria) {
        Specification<Recipe> spec = (root, query, cb) ->
            cb.conjunction();

        if(criteria == null) return spec;

        if(criteria.name() != null && !criteria.name().isBlank()) {
            spec = spec.and(hasName(criteria.name()));
        }

        if(criteria.author() != null && !criteria.author().isBlank()) {
            spec = spec.and(hasAuthor(criteria.author()));
        }

        if(criteria.maxPrepTime() != null) {
            spec = spec.and(hasPrepTime(criteria.maxPrepTime()));
        }

        if(criteria.minRating() != null) {
            spec = spec.and(hasRating(criteria.minRating()));
        }

        if(criteria.tags() != null && !criteria.tags().isEmpty()) {
            spec = spec.and(containsTag(criteria.tags()));
        }

        if(criteria.categories() != null && !criteria.categories().isEmpty()) {
            spec = spec.and(containsCategory(criteria.categories()));
        }

        return spec;
    }
}
