package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewUpdateRequest;
import io.github.filipolszewski.cookbook.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1)
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/recipes/{slug}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getRecipeReviews(
            @PathVariable String slug,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getRecipeReviews(slug, pageable));
    }

    @GetMapping("/users/{username}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            @PathVariable String username,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getUserReviews(username, pageable));
    }

    @PostMapping("/recipes/{slug}/reviews")
    public ResponseEntity<ReviewResponse> postReview(
            @PathVariable String slug,
            @RequestBody @Valid ReviewPostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.postReview(slug, request));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateRequest request
    ) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

}
