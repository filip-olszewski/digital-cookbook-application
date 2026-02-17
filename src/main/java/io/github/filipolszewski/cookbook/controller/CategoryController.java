package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.category.CategoryCreateRequest;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.category.CategoryUpdateRequest;
import io.github.filipolszewski.cookbook.service.CategoryService;
import io.github.filipolszewski.cookbook.specification.criteria.CategorySearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/categories")
@Tag(name = "Categories", description = "Endpoints for managing recipe categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get list of categories", description = "Public endpoint to retrieve all categories based on optional search criteria.")
    @GetMapping
    public ResponseEntity<List<CategorySummaryResponse>> getCategories(
            @ParameterObject CategorySearchCriteria criteria
    ) {
        return ResponseEntity.ok(categoryService.getCategories(criteria));
    }

    @Operation(summary = "Get category details", description = "Public endpoint to retrieve a specific category by its slug.")
    @GetMapping("/{slug}")
    public ResponseEntity<CategoryDetailsResponse> getCategory(
            @PathVariable String slug
    ) {
        return ResponseEntity.ok(categoryService.getCategory(slug));
    }

    @Operation(
        summary = "Create a new category",
        description = "Admin only. Requires a valid JWT token with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<CategorySummaryResponse> createCategory(
            @RequestBody @Valid CategoryCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @Operation(
        summary = "Delete a category",
        description = "Admin only. Requires a valid JWT token with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update a category",
        description = "Admin only. Requires a valid JWT token with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDetailsResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }
}