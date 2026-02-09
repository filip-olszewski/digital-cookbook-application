package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeCreateRequest;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeUpdateRequest;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.RecipeIngredientMapper;
import io.github.filipolszewski.cookbook.mapper.RecipeMapper;
import io.github.filipolszewski.cookbook.mapper.StepMapper;
import io.github.filipolszewski.cookbook.model.entity.*;
import io.github.filipolszewski.cookbook.repository.*;
import io.github.filipolszewski.cookbook.security.UserContext;
import io.github.filipolszewski.cookbook.specification.SpecificationBuilder;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final SpecificationBuilder<Recipe, RecipeSearchCriteria> specificationBuilder;

    private final StepMapper stepMapper;

    private final UserService userService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final RecipeIngredientService recipeIngredientService;
    private final RecipeSlugService slugService;

    private final UserContext userContext;


    public Page<RecipeSummaryResponse> getRecipes(RecipeSearchCriteria criteria, Pageable pageable) {
        return recipeRepository.findAll(specificationBuilder.build(criteria), pageable)
                .map(recipeMapper::toSummary);
    }

    public RecipeDetailsResponse getRecipe(String slug) {
        return recipeRepository.findBySlug(slug)
            .map(recipeMapper::toDetails)
            .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorMessageUtil.notFound(Recipe.class, "slug", slug)));
    }

    @Transactional
    public RecipeSummaryResponse createRecipe(RecipeCreateRequest request) {

        Recipe recipe = recipeMapper.toEntity(request);

        User currentUser = userService.findUserById(userContext.getCurrentUserId());
        recipe.setAuthor(currentUser);

        String slug = slugService.generate(request.name());
        recipe.setSlug(slug);

        updateCategory(recipe, request.categoryId());
        updateTags(recipe, request.tagIds());
        updateIngredients(recipe, request.recipeIngredients());
        updateSteps(recipe, request.steps());

        recipe.setPublicationDate(LocalDate.now());

        return recipeMapper.toSummary(recipeRepository.save(recipe));
    }

    @Transactional
    @PreAuthorize("@recipeSecurity.isAuthorOrAdmin(#id, authentication)")
    public void deleteRecipe(Long id) {
        Recipe recipe = findRecipeById(id);
        recipeRepository.delete(recipe);
    }

    @Transactional
    @PreAuthorize("@recipeSecurity.isAuthor(#id, authentication)")
    public RecipeDetailsResponse updateRecipe(Long id, RecipeUpdateRequest request) {
        Recipe recipe = findRecipeById(id);

        recipeMapper.update(recipe, request);

        updateCategory(recipe, request.categoryId());
        updateTags(recipe, request.tagIds());
        updateIngredients(recipe, request.recipeIngredients());
        updateSteps(recipe, request.steps());

        return recipeMapper.toDetails(recipeRepository.save(recipe));
    }

    private Recipe findRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Recipe.class, "id", id)));
    }

    private void updateCategory(Recipe recipe, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryService.findCategoryById(categoryId);
            recipe.setCategory(category);
        }
    }

    private void updateTags(Recipe recipe, List<Long> tagIds) {
        if (tagIds != null) {
            Set<Tag> tags = tagService.findTagsByIds(tagIds);
            recipe.replaceTags(tags);
        }
    }

    private void updateIngredients(Recipe recipe, List<RecipeIngredientAddRequest> requests) {
        if (requests != null) {
            List<RecipeIngredient> ingredients = recipeIngredientService.assembleIngredients(requests);
            recipe.replaceIngredients(ingredients);
        }
    }

    private void updateSteps(Recipe recipe, List<StepAppendRequest> requests) {
        if (requests != null) {
            List<Step> newSteps = requests.stream()
                    .map(req -> {
                        Step step = stepMapper.toEntity(req);
                        step.setRecipe(recipe);
                        return step;
                    })
                    .toList();
            recipe.replaceSteps(newSteps);
        }
    }
}
