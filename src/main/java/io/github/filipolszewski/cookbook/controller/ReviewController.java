package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewUpdateRequest;
import io.github.filipolszewski.cookbook.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1)
@Tag(name = "Reviews", description = "Endpoints for managing recipe reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Get reviews for a recipe", description = "Public endpoint to get a paged list of reviews for a specific recipe.")
    @GetMapping("/recipes/{slug}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getRecipeReviews(
            @PathVariable String slug,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getRecipeReviews(slug, pageable));
    }

    @Operation(summary = "Get reviews by a user", description = "Public endpoint to get a paged list of reviews written by a specific user.")
    @GetMapping("/users/{username}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            @PathVariable String username,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getUserReviews(username, pageable));
    }

    @Operation(
        summary = "Post a review",
        description = "Requires a valid JWT token.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/recipes/{slug}/reviews")
    public ResponseEntity<ReviewResponse> postReview(
            @PathVariable String slug,
            @RequestBody @Valid ReviewPostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.postReview(slug, request));
    }

    @Operation(
        summary = "Delete a review",
        description = "Requires a valid JWT token. Users can only delete their own reviews.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update a review",
        description = "Requires a valid JWT token. Users can only update their own reviews.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PatchMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }
}