package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceConflictException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.ReviewMapper;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.ReviewRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.UserContext;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import io.github.filipolszewski.cookbook.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    private final UserContext userContext;

    public Page<ReviewResponse> getRecipeReviews(String slug, Pageable pageable) {
        log.debug("Fetching reviews for recipe slug: {}", slug);
        return reviewRepository.findAllReviewsByRecipeSlugWithUser(slug, pageable)
                .map(reviewMapper::toResponse);
    }

    public Page<ReviewResponse> getUserReviews(String username, Pageable pageable) {
        log.debug("Fetching reviews for user: {}", username);
        return reviewRepository.findAllReviewsByUserUsernameWithUser(username, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional
    public ReviewResponse postReview(String recipeSlug, ReviewPostRequest request) {
        Long currentUserId = userContext.getCurrentUserId();

        log.info("User [{}] is posting a review for recipe [{}]", currentUserId, recipeSlug);

        Recipe recipe = recipeRepository.findBySlug(recipeSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Recipe.class, "slug", recipeSlug)));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(User.class, "id", currentUserId)));

        // User can only review a recipe once
        if(reviewRepository.existsByUserIdAndRecipeId(currentUserId, recipe.getId())) {
            log.warn("Review failed: User [{}] already reviewed recipe [{}]", currentUserId, recipeSlug);
            throw new ResourceConflictException("User already reviewed this recipe!");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(currentUser);
        review.setRecipe(recipe);

        Review saved = reviewRepository.save(review);
        recipeRepository.addReviewRating(recipe.getId(), request.rating());

        log.info("Review created successfully with ID: [{}]", saved.getId());

        return reviewMapper.toResponse(saved);
    }

    @Transactional
    @PreAuthorize("@reviewSecurity.isAuthorOrAdmin(#id, authentication)")
    public void deleteReview(Long id) {
        log.info("Processing deletion for review ID: {}", id);

        Review review = reviewRepository.findByIdWithRecipe(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Review.class, "id", id)));

        Recipe recipe = review.getRecipe();

        recipeRepository.removeReviewRating(recipe.getId(), review.getRating());
        reviewRepository.delete(review);

        log.info("Successfully deleted review ID: {}", id);
    }

    @Transactional
    @PreAuthorize("@reviewSecurity.isAuthor(#id, authentication)")
    public ReviewResponse updateReview(Long id, ReviewUpdateRequest request) {
        log.info("Processing update for review ID: {}", id);

        Review review = reviewRepository.findByIdWithRecipeAndUser(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Review.class, "id", id)));

        int oldRating = review.getRating();
        reviewMapper.update(review, request);

        if(oldRating != review.getRating()) {
            recipeRepository.updateReviewRating(review.getRecipe().getId(), oldRating, review.getRating());
            log.info("Review ID: {} rating changed from {} to {}", id, oldRating, review.getRating());
        }

        Review updated = reviewRepository.save(review);
        log.info("Successfully updated review ID: {}", id);

        return reviewMapper.toResponse(updated);
    }
}