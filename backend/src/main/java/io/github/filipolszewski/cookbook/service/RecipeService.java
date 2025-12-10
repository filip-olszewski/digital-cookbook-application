package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.RecipeMapper;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.specification.SpecificationBuilder;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final SpecificationBuilder<Recipe, RecipeSearchCriteria> specificationBuilder;

    public Page<RecipeSummaryResponse> getRecipes(RecipeSearchCriteria criteria, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAll(specificationBuilder.build(criteria), pageable);
        return recipes.map(recipeMapper::toSummary);
    }

    public RecipeDetailsResponse getRecipe(String slug) {
        return recipeRepository.findBySlug(slug)
            .map(recipeMapper::toDetails)
            .orElseThrow(() ->
                new ResourceNotFoundException("Recipe with a slug of " + slug + " not found."));
    }
}
