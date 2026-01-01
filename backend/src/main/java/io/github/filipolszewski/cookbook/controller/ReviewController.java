package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
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
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable String slug,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getReviews(slug, pageable));
    }

    @PostMapping("/recipes/{slug}/reviews")
    public ResponseEntity<ReviewResponse> postReview(
            @PathVariable String slug,
            @RequestBody @Valid ReviewPostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.postReview(slug, request));
    }

    @DeleteMapping("/recipes/{slug}/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String slug,
            @PathVariable Long id
    ) {
        reviewService.deleteReview(slug, id);
        return ResponseEntity.noContent().build();
    }

}
