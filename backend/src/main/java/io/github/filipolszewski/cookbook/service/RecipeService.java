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

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final IngredientRepository ingredientRepository;

    private final RecipeIngredientMapper recipeIngredientMapper;
    private final StepMapper stepMapper;

    private final UserContext userContext;

    private final Slugify slugify = Slugify.builder().build();

    public Page<RecipeSummaryResponse> getRecipes(RecipeSearchCriteria criteria, Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAll(specificationBuilder.build(criteria), pageable);
        return recipes.map(recipeMapper::toSummary);
    }

    public RecipeDetailsResponse getRecipe(String slug) {
        return recipeRepository.findBySlug(slug)
            .map(recipeMapper::toDetails)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorMessageUtil.notFound(Recipe.class, "slug", slug)
            ));
    }

    @Transactional
    public RecipeSummaryResponse createRecipe(RecipeCreateRequest request) {

        Recipe recipe = recipeMapper.toEntity(request);
        recipe.setAuthor(userContext.getCurrentUser());
        recipe.setPublicationDate(LocalDate.now());
        recipe.setSlug(generateUniqueSlug(request.name()));

        recipe.setCategory(findCategory(request.categoryId()));
        recipe.replaceTags(findTags(request.tagIds()));
        recipe.replaceRecipeIngredients(mapRecipeIngredients(request.recipeIngredients(), recipe));
        recipe.replaceSteps(mapSteps(request.steps(), recipe));

        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toSummary(saved);
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

        recipeMapper.updateBasicFields(recipe, request);

        if(request.name() != null &&
          !request.name().isBlank() &&
          !request.name().equals(recipe.getName())) {

            recipe.setName(request.name());

            String newSlug = slugify.slugify(request.name());

            if (!recipe.getSlug().equals(newSlug)) {
                recipe.setSlug(generateUniqueSlug(request.name()));
            }
        }

        if(request.categoryId() != null) {
            recipe.setCategory(findCategory(request.categoryId()));
        }

        if(request.tagIds() != null) {
            recipe.replaceTags(findTags(request.tagIds()));
        }

        if(request.recipeIngredients() != null) {
            recipe.replaceRecipeIngredients(mapRecipeIngredients(request.recipeIngredients(), recipe));
        }

        if(request.steps() != null) {
            recipe.replaceSteps(mapSteps(request.steps(), recipe));
        }

        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toDetails(saved);
    }


    private String generateUniqueSlug(String name) {
        String slug = slugify.slugify(name);
        List<String> takenSlugs = recipeRepository.findSlugsStartingWith(slug);

        if (!takenSlugs.contains(slug)) {
            return slug;
        }

        int counter = 1;
        String candidate = slug + "-" + counter;

        while (takenSlugs.contains(candidate)) {
            counter++;
            candidate = slug + "-" + counter;
        }

        return candidate;
    }

    private Recipe findRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Recipe.class, "id", id)));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));
    }

    private Set<Tag> findTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return Set.of();

        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found");
        }

        return new HashSet<>(tags);
    }

    private List<RecipeIngredient> mapRecipeIngredients(
            List<RecipeIngredientAddRequest> requests,
            Recipe recipe
    ) {

        Set<Long> ingredientIds = requests.stream().map(RecipeIngredientAddRequest::ingredientId)
                .collect(Collectors.toSet());

        Map<Long, Ingredient> ingredientMap = ingredientRepository.findAllById(ingredientIds)
                .stream()
                .collect(Collectors.toMap(BaseEntity::getId, i -> i));

        if (ingredientMap.size() != ingredientIds.size()) {
            throw new ResourceNotFoundException("One or more ingredients not found");
        }

        return requests.stream()
                .map(req -> {
                    RecipeIngredient r = recipeIngredientMapper.toEntity(req);
                    r.setIngredient(ingredientMap.get(req.ingredientId()));
                    return r;
                })
                .toList();
    }

    private List<Step> mapSteps(List<StepAppendRequest> requests, Recipe recipe) {
        return requests.stream()
                .map(req -> {
                    Step step = stepMapper.toEntity(req);
                    step.setRecipe(recipe);
                    return step;
                })
                .toList();
    }
}
