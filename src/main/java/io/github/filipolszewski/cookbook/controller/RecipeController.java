package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeCreateRequest;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeUpdateRequest;
import io.github.filipolszewski.cookbook.service.RecipeService;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/recipes")
@Tag(name = "Recipes", description = "Endpoint for managing recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "Get a paged list of recipes")
    @GetMapping
    public ResponseEntity<Page<RecipeSummaryResponse>> getRecipes(
        @ParameterObject RecipeSearchCriteria criteria,
        @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.getRecipes(criteria, pageable));
    }

    @Operation(
        summary = "Get recipe details",
        description = "Public endpoint to retrieve complete details of a specific recipe by its slug."
    )
    @GetMapping("/{slug}")
    public ResponseEntity<RecipeDetailsResponse> getRecipe(
        @PathVariable String slug
    ) {
        return ResponseEntity.ok(recipeService.getRecipe(slug));
    }

    @Operation(
        summary = "Create a new recipe",
        description = "Creates a new recipe. Requires a valid JWT token.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping
    public ResponseEntity<RecipeSummaryResponse> createRecipe(
            @RequestBody @Valid RecipeCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(request));
    }

    @Operation(
        summary = "Delete a recipe",
        description = "Deletes a recipe by its ID. Requires a valid JWT token.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id
    ) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update a recipe",
        description = "Partially updates an existing recipe. Requires a valid JWT token.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PatchMapping("/{id}")
    public ResponseEntity<RecipeDetailsResponse> updateRecipe(
            @PathVariable Long id,
            @RequestBody @Valid RecipeUpdateRequest request
    ) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, request));
    }
}
