package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientUpdateRequest;
import io.github.filipolszewski.cookbook.service.IngredientService;
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
@RequestMapping(ApiConstants.API_V1 + "/ingredients")
@Tag(name = "Ingredients", description = "Endpoints for managing ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(summary = "Get a paged list of ingredients", description = "Public endpoint. Can optionally filter by name.")
    @GetMapping
    public ResponseEntity<Page<IngredientSummaryResponse>> getIngredients(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(ingredientService.getIngredients(pageable, name));
    }

    @Operation(
        summary = "Add a new ingredient",
        description = "Admin only. Requires a valid JWT with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<IngredientSummaryResponse> addIngredient(
            @RequestBody @Valid IngredientCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.addIngredient(request));
    }

    @Operation(
        summary = "Delete an ingredient",
        description = "Admin only. Requires a valid JWT with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long id
    ) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update an ingredient",
        description = "Admin only. Requires a valid JWT with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PatchMapping("/{id}")
    public ResponseEntity<IngredientSummaryResponse> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientUpdateRequest request
    ) {
        return ResponseEntity.ok(ingredientService.updateIngredient(id, request));
    }
}