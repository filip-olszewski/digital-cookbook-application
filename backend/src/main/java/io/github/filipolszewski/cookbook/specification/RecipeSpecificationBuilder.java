package io.github.filipolszewski.cookbook.specification;

import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

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

        return spec;
    }
}
