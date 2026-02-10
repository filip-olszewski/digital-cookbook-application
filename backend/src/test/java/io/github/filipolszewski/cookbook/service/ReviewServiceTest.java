package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewUpdateRequest;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void getRecipeReviews_WhenRecipeHasReviews_ShouldReturnPagedReviews() {
        Long id = 1L;
        String recipeSlug = "dinner";
        Pageable pageable = Pageable.ofSize(10);

        Recipe recipe = new Recipe();
        recipe.setSlug(recipeSlug);

        Review review = new Review();
        review.setId(id);
        review.setRecipe(recipe);

        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        var dto = new ReviewResponse(id, 5, "comment", Instant.now(),
                new UserSummaryResponse(1L, "username", "fullName")
        );

        Page<ReviewResponse> expected = new PageImpl<>(List.of(dto));

        when(reviewRepository.findAllReviewsByRecipeSlugWithUser(recipeSlug, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toResponse(review)).thenReturn(dto);

        var res = reviewService.getRecipeReviews(recipeSlug, pageable);

        assertThat(res).isEqualTo(expected);
        verify(reviewRepository).findAllReviewsByRecipeSlugWithUser(any(), any());
    }

    @Test
    void getRecipeReviews_WhenRecipeDoesNotHaveReviews_ShouldReturnEmptyPage() {
        String recipeSlug = "dinner";
        Pageable pageable = Pageable.ofSize(10);

        Recipe recipe = new Recipe();
        recipe.setSlug(recipeSlug);

        Page<Review> reviewPage = Page.empty();
        Page<ReviewResponse> expected = Page.empty();

        when(reviewRepository.findAllReviewsByRecipeSlugWithUser(recipeSlug, pageable)).thenReturn(reviewPage);

        var res = reviewService.getRecipeReviews(recipeSlug, pageable);

        assertThat(res).isEqualTo(expected);
        assertThat(res.getTotalElements()).isEqualTo(0);
        verify(reviewRepository).findAllReviewsByRecipeSlugWithUser(any(), any());
        verify(reviewMapper, never()).toResponse(any());
    }

    @Test
    void getUserReviews_WhenUserHasReviews_ShouldReturnPagedReviews() {
        Long id = 1L;
        String username = "user123";
        Pageable pageable = Pageable.ofSize(10);

        User user = new User();
        user.setUsername(username);

        Review review = new Review();
        review.setId(id);
        review.setUser(user);

        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        var dto = new ReviewResponse(id, 5, "comment", Instant.now(),
                new UserSummaryResponse(1L, username, "fullName")
        );

        Page<ReviewResponse> expected = new PageImpl<>(List.of(dto));

        when(reviewRepository.findAllReviewsByUserUsernameWithUser(username, pageable)).thenReturn(reviewPage);
        when(reviewMapper.toResponse(review)).thenReturn(dto);

        var res = reviewService.getUserReviews(username, pageable);

        assertThat(res).isEqualTo(expected);
        assertThat(res.getContent().getFirst().user().username()).isEqualTo(username);
        verify(reviewRepository).findAllReviewsByUserUsernameWithUser(any(), any());
    }

    @Test
    void getUserReviews_WhenUserDoesNotHaveReviews_ShouldReturnEmptyPage() {
        String username = "user123";
        Pageable pageable = Pageable.ofSize(10);

        User user = new User();
        user.setUsername(username);

        Page<Review> reviewPage = Page.empty();
        Page<ReviewResponse> expected = Page.empty();

        when(reviewRepository.findAllReviewsByUserUsernameWithUser(username, pageable)).thenReturn(reviewPage);

        var res = reviewService.getUserReviews(username, pageable);

        assertThat(res).isEqualTo(expected);
        assertThat(res.getTotalElements()).isEqualTo(0);
        verify(reviewRepository).findAllReviewsByUserUsernameWithUser(any(), any());
        verify(reviewMapper, never()).toResponse(any());
    }

    @Test
    void postReview_WhenUserHasNotReviewedYet_ShouldSaveReview() {
        Long reviewId = 1L;
        Long userId = 1L;
        Long recipeId = 1L;
        String recipeSlug = "recipe";
        int reviewRating = 5;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setSlug(recipeSlug);
        recipe.setReviewCount(1);
        recipe.setAverageRating(3.0);

        User user = new User();
        user.setId(userId);

        var request = new ReviewPostRequest(reviewRating, null);

        Review review = new Review();
        review.setRating(reviewRating);

        Review saved = new Review();
        saved.setId(reviewId);
        saved.setRating(reviewRating);
        saved.setUser(user);
        saved.setRecipe(recipe);

        var expected = new ReviewResponse(reviewId, reviewRating, null, Instant.now(), new UserSummaryResponse(
            userId, "user123", "fullName"
        ));

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(recipeRepository.findBySlug(recipeSlug)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);
        when(reviewMapper.toEntity(request)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(saved);
        doNothing().when(recipeRepository).addReviewRating(recipeId, reviewRating);
        when(reviewMapper.toResponse(saved)).thenReturn(expected);

        var res = reviewService.postReview(recipeSlug, request);

        assertThat(res).isEqualTo(expected);
        verify(recipeRepository).addReviewRating(recipeId, reviewRating);
        verify(reviewRepository).save(eq(review));
    }

    @Test
    void postReview_WhenRecipeNotFound_ShouldThrowResourceNotFoundException() {
        String slug = "unknown-recipe";
        Long userId = 1L;
        var request = new ReviewPostRequest(5, "Great!");

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(recipeRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.postReview(slug, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Recipe")
                .hasMessageContaining(slug);

        verify(userRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void postReview_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        String slug = "chocolate-cake";
        Long userId = 99L;
        var request = new ReviewPostRequest(5, "Great!");

        Recipe recipe = new Recipe();
        recipe.setId(1L);

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(recipeRepository.findBySlug(slug)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.postReview(slug, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User")
                .hasMessageContaining(String.valueOf(userId));

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void postReview_WhenUserAlreadyReviewed_ShouldThrowResourceConflictException() {
        String recipeSlug = "recipe";
        Long userId = 1L;
        Long recipeId = 1L;

        var request = new ReviewPostRequest(5, "comment");

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        User user = new User();
        user.setId(userId);

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(recipeRepository.findBySlug(recipeSlug)).thenReturn(Optional.of(recipe));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.postReview(recipeSlug, request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("User already reviewed this recipe!");

        verify(reviewRepository, never()).save(any());
        verify(recipeRepository, never()).addReviewRating(anyLong(), anyInt());
    }

    @Test
    void deleteReview_WhenReviewExists_ShouldDeleteReviewAndRemoveRating() {
        Long reviewId = 1L;
        Long recipeId = 1L;
        int rating = 5;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Review review = new Review();
        review.setId(reviewId);
        review.setRating(rating);
        review.setRecipe(recipe);

        when(reviewRepository.findByIdWithRecipe(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(reviewId);

        verify(reviewRepository).delete(eq(review));
        verify(recipeRepository).removeReviewRating(eq(recipeId), eq(rating));
    }

    @Test
    void deleteReview_WhenReviewNotFound_ShouldThrowResourceNotFoundException() {
        Long reviewId = 1L;

        when(reviewRepository.findByIdWithRecipe(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(reviewId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review")
                .hasMessageContaining(String.valueOf(reviewId));

        verify(recipeRepository, never()).removeReviewRating(anyLong(), anyInt());
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void updateReview_WhenRatingChanged_ShouldUpdateRecipeStats() {
        Long reviewId = 1L;
        Long recipeId = 100L;
        int oldRating = 3;
        int newRating = 5;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);

        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setRating(oldRating);
        existingReview.setRecipe(recipe);

        ReviewUpdateRequest request = new ReviewUpdateRequest(newRating, null);

        doAnswer(invocation -> {
            Review reviewArg = invocation.getArgument(0);
            ReviewUpdateRequest reqArg = invocation.getArgument(1);
            reviewArg.setRating(reqArg.rating());
            return null;
        }).when(reviewMapper).update(any(Review.class), any(ReviewUpdateRequest.class));

        when(reviewRepository.findByIdWithRecipeAndUser(reviewId))
                .thenReturn(Optional.of(existingReview));

        when(reviewMapper.toResponse(any())).thenReturn(mock(ReviewResponse.class));

        reviewService.updateReview(reviewId, request);

        verify(recipeRepository).updateReviewRating(recipeId, oldRating, newRating);
        assertThat(existingReview.getRating()).isEqualTo(newRating);
    }

    @Test
    void updateReview_WhenOnlyCommentChanged_ShouldNotUpdateStats() {
        Long reviewId = 1L;
        int rating = 5;

        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setRating(rating);
        existingReview.setComment("Old text");
        existingReview.setRecipe(new Recipe());

        ReviewUpdateRequest request = new ReviewUpdateRequest(
                rating,
                JsonNullable.of("New updated text")
        );

        doAnswer(invocation -> {
            Review reviewArg = invocation.getArgument(0);
            reviewArg.setComment("New updated text");
            return null;
        }).when(reviewMapper).update(any(Review.class), any(ReviewUpdateRequest.class));

        when(reviewRepository.findByIdWithRecipeAndUser(reviewId))
                .thenReturn(Optional.of(existingReview));

        reviewService.updateReview(reviewId, request);

        assertThat(existingReview.getComment()).isEqualTo("New updated text");
        verify(recipeRepository, never()).updateReviewRating(anyLong(), anyInt(), anyInt());
    }

    @Test
    void updateReview_WhenReviewNotFound_ShouldThrowException() {
        Long reviewId = 99L;
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, null);

        when(reviewRepository.findByIdWithRecipeAndUser(reviewId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(reviewId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review");

        verifyNoInteractions(recipeRepository);
    }
}