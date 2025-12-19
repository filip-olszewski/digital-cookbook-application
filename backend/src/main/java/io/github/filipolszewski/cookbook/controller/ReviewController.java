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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1)
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/recipes/{slug}/reviews")
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(
            @PathVariable String slug,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getAllReviews(slug, pageable));
    }

    @PostMapping("/recipes/{slug}/reviews")
    public ResponseEntity<ReviewResponse> postReview(
            @PathVariable String slug,
            @RequestBody @Valid ReviewPostRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.postReview(slug, request));
    }
}
