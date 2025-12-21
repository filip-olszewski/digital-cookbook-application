package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.ReviewMapper;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final RecipeRepository recipeRepository;

    public Page<ReviewResponse> getReviews(String slug, Pageable pageable) {
        return reviewRepository.findAllReviewsByRecipeSlug(slug, pageable)
                .map(review -> reviewMapper.toResponse(review));
    }

    @Transactional
    public ReviewResponse postReview(String slug, ReviewPostRequest request) {
        Recipe recipe = recipeRepository.findBySlug(slug).orElseThrow(() ->
            new ResourceNotFoundException("Recipe with a slug of " + slug + " does not exist."));

        // Map to Entity
        Review review = reviewMapper.toEntity(request);
        review.setRecipe(recipe);
//      review.setUser();
        reviewRepository.save(review);

        // Calculate new recipe rating
        updateRecipeRating(recipe);

        return reviewMapper.toResponse(review);
    }

    private void updateRecipeRating(Recipe recipe) {
        Double avgRating = recipeRepository.getRecipeAverageRating(recipe.getId());
        Integer reviewCount = recipeRepository.getRecipeReviewCount(recipe.getId());

        recipe.setAverageRating(avgRating != null ? avgRating : 0.0);
        recipe.setReviewCount(reviewCount);

        recipeRepository.save(recipe);
    }
}
