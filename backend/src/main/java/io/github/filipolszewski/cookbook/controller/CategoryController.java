package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constants.ApiConstants;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.service.CategoryService;
import io.github.filipolszewski.cookbook.specification.criteria.CategorySearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategorySummaryResponse>> getAllCategories(
        @ModelAttribute CategorySearchCriteria criteria
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(criteria));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryDetailsResponse> getCategory(
        @PathVariable String slug
    ) {
        return ResponseEntity.ok(categoryService.getCategory(slug));
    }

}
