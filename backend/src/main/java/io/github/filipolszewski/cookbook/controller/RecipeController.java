package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.service.RecipeService;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeSummaryResponse>> getRecipes(
        @ParameterObject RecipeSearchCriteria criteria,
        @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(recipeService.getRecipes(criteria, pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<RecipeDetailsResponse> getRecipe(
        @PathVariable String slug
    ) {
        return ResponseEntity.ok(recipeService.getRecipe(slug));
    }

}
