package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constants.ApiConstants;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.service.RecipeService;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeSummaryResponse>> getRecipes(RecipeSearchCriteria criteria, Pageable pageable) {
        return ResponseEntity.ok(recipeService.getRecipes(criteria, pageable));
    }

}
