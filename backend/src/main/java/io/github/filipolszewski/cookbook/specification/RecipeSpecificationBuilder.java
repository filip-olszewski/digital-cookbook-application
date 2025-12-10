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

    private Specification<Recipe> hasPrepTime(Integer maxPrepTime) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("prepTime"), maxPrepTime);
    }

    private Specification<Recipe> hasRating(Double minRating) {
        return (root, query, cb) -> {

            Join<Recipe, Review> reviewsTable = root.join("reviews", JoinType.INNER);
            query.groupBy(root.get("id"));
            query.having(cb.greaterThanOrEqualTo(cb.avg(reviewsTable.get("rating")), minRating));

            return cb.conjunction();
        };
    }

    private Specification<Recipe> containsTag(List<Long> tagIds) {
        return (root, query, cb) -> {

            Join<Tag, Recipe> tags = root.join("tags", JoinType.LEFT);
            return tags.get("id").in(tagIds);
        };
    }

    private Specification<Recipe> containsCategory(List<Long> categoryIds) {
        return (root, query, cb) ->
            root.get("category").get("id").in(categoryIds);
    }

    @Override
    public Specification<Recipe> build(RecipeSearchCriteria criteria) {
        Specification<Recipe> spec = (root, query, cb) ->
            cb.conjunction();

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
