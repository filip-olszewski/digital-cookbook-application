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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return reviewRepository.findAllReviewsByRecipeSlug(slug, pageable)
                .map(reviewMapper::toResponse);
    }

    public Page<ReviewResponse> getUserReviews(String username, Pageable pageable) {
        return reviewRepository.findAllReviewsByUserUsername(username, pageable)
                .map(reviewMapper::toResponse);
    }

    @Transactional
    public ReviewResponse postReview(String slug, ReviewPostRequest request) {

        Recipe recipe = recipeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Recipe.class, "slug", slug)));

        Long currentUserId = userContext.getCurrentUserId();
        User currentUser = userRepository.getReferenceById(currentUserId);

        // User can only review a recipe once
        if(reviewRepository.existsByUserIdAndRecipeId(currentUserId, recipe.getId())) {
            throw new ResourceConflictException("User already reviewed this recipe!");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUser(currentUser);
        review.setRecipe(recipe);

        Review saved = reviewRepository.save(review);

        // Calculate new recipe rating
        // Dirty checking saves recipe automatically
        updateRecipeStatistics(recipe, request.rating());

        return reviewMapper.toResponse(saved);
    }

    @Transactional
    @PreAuthorize("@reviewSecurity.isAuthorOrAdmin(#id, authentication)")
    public void deleteReview(String slug, Long id) {

        // TODO: START FROM HERE
        // Load recipe to memory to udpate the stats
        // No need to load review to memory?

        Review review = findReviewById(id);
        validateReviewBelongsToRecipe(review, slug);

        Recipe recipe = review.getRecipe();

        reviewRepository.delete(review);
        reviewRepository.flush();

        recalculateRecipeRating(recipe);
    }

    @Transactional
    @PreAuthorize("@reviewSecurity.isAuthor(#id, authentication)")
    public ReviewResponse updateReview(String slug, Long id, ReviewUpdateRequest request) {

        Review review = findReviewById(id);
        validateReviewBelongsToRecipe(review, slug);

        boolean ratingChanged = false;

        if(request.rating() != null &&
          !request.rating().equals(review.getRating())) {
            review.setRating(request.rating());
            ratingChanged = true;
        }

        if(request.comment() != null &&
          !request.comment().isBlank() &&
          !request.comment().equals(review.getComment())) {
            review.setComment(request.comment());
        }

        Review saved = reviewRepository.saveAndFlush(review);

        if(ratingChanged) {
            recalculateRecipeRating(review.getRecipe());
        }

        return reviewMapper.toResponse(saved);
    }


    private void updateRecipeStatistics(Recipe recipe, int newRating) {
        int oldCount = recipe.getReviewCount();
        int newCount = oldCount + 1;
        double oldAvg = recipe.getAverageRating();
        double newAvg = ((oldAvg * oldCount) + newRating) / newCount;
        recipe.setReviewCount(newCount);
        recipe.setAverageRating(newAvg);
    }

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Review.class, "id", id)));
    }

    private void validateReviewBelongsToRecipe(Review review, String recipeSlug) {
        if (!review.getRecipe().getSlug().equals(recipeSlug)) {
            throw new ResourceNotFoundException("Review does not belong to the specified recipe.");
        }
    }
}
