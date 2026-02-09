package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.recipe.RecipeCreateRequest;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.RecipeMapper;
import io.github.filipolszewski.cookbook.mapper.StepMapper;
import io.github.filipolszewski.cookbook.model.entity.*;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.security.UserContext;
import io.github.filipolszewski.cookbook.specification.SpecificationBuilder;
import io.github.filipolszewski.cookbook.specification.criteria.RecipeSearchCriteria;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock private RecipeRepository recipeRepository;
    @Mock private RecipeMapper recipeMapper;
    @Mock private SpecificationBuilder<Recipe, RecipeSearchCriteria> specificationBuilder;
    @Mock private UserService userService;
    @Mock private CategoryService categoryService;
    @Mock private RecipeSlugService slugService;
    @Mock private RecipeIngredientService recipeIngredientService;
    @Mock private TagService tagService;
    @Mock private UserContext userContext;
    @Mock private StepMapper stepMapper;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    @SuppressWarnings("unchecked")
    void getRecipes_WhenNoCriteriaProvided_ShouldReturnPagedRecipes() {
        Pageable pageable = Pageable.ofSize(10);
        Specification<Recipe> spec = mock(Specification.class);
        RecipeSearchCriteria criteria = null;

        Recipe recipe = new Recipe();

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));
        var dto = Instancio.create(RecipeSummaryResponse.class);
        Page<RecipeSummaryResponse> expected = new PageImpl<>(List.of(dto));

        when(specificationBuilder.build(criteria)).thenReturn(spec);
        when(recipeRepository.findAll(spec, pageable)).thenReturn(recipePage);
        when(recipeMapper.toSummary(recipe)).thenReturn(dto);

        var res = recipeService.getRecipes(criteria, pageable);

        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
        verify(specificationBuilder).build(criteria);
        verify(recipeRepository).findAll(spec, pageable);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRecipes_WhenCriteriaProvided_ShouldFilterAndReturnPagedRecipes() {
        Pageable pageable = Pageable.ofSize(10);

        RecipeSearchCriteria criteria = new RecipeSearchCriteria(
            "pancakes", null, null, null, null, null
        );

        Specification<Recipe> spec = mock(Specification.class);

        Recipe recipe = new Recipe();
        var dto = Instancio.create(RecipeSummaryResponse.class);

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));
        Page<RecipeSummaryResponse> expected = new PageImpl<>(List.of(dto));

        when(specificationBuilder.build(criteria)).thenReturn(spec);
        when(recipeRepository.findAll(spec, pageable)).thenReturn(recipePage);
        when(recipeMapper.toSummary(recipe)).thenReturn(dto);

        var res = recipeService.getRecipes(criteria, pageable);

        assertThat(res).usingRecursiveComparison().isEqualTo(expected);
        verify(specificationBuilder).build(criteria);
        verify(recipeRepository).findAll(spec, pageable);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getRecipes_WhenNoRecipesFound_ShouldReturnEmptyPage() {
        Pageable pageable = Pageable.ofSize(10);
        RecipeSearchCriteria criteria = null;
        Specification<Recipe> spec = mock(Specification.class);

        Page<Recipe> emptyRecipePage = Page.empty(pageable);

        when(specificationBuilder.build(criteria)).thenReturn(spec);
        when(recipeRepository.findAll(spec, pageable)).thenReturn(emptyRecipePage);

        var res = recipeService.getRecipes(criteria, pageable);

        assertThat(res.getTotalElements()).isZero();
        assertThat(res.getContent()).isEmpty();

        verify(recipeMapper, never()).toSummary(any());
    }

    @Test
    void getRecipe_WhenRecipeExists_ShouldReturnDetails() {
        String slug = "pancakes";
        Recipe recipe = new Recipe();
        recipe.setSlug(slug);

        RecipeDetailsResponse expectedResponse = Instancio.create(RecipeDetailsResponse.class);

        when(recipeRepository.findBySlug(slug)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toDetails(recipe)).thenReturn(expectedResponse);

        RecipeDetailsResponse result = recipeService.getRecipe(slug);

        assertThat(result).isEqualTo(expectedResponse);
        verify(recipeRepository).findBySlug(slug);
        verify(recipeMapper).toDetails(recipe);
    }

    @Test
    void getRecipe_WhenRecipeNotFound_ShouldThrowException() {
        String slug = "pancakes";

        when(recipeRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getRecipe(slug))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Recipe", slug);

        verify(recipeMapper, never()).toDetails(any(Recipe.class));
    }

    @Test
    void createRecipe_WhenRequestValid_ShouldCreateAndSave() {
        Long userId = 100L;
        Long categoryId = 10L;
        List<Long> tagIds = List.of(50L, 51L);
        String generatedSlug = "pancakes-slug";

        RecipeCreateRequest request = createValidRequest(categoryId, tagIds);

        User author = new User(); author.setId(userId);
        Category category = new Category(); category.setId(categoryId);
        Set<Tag> tags = Set.of(new Tag(), new Tag());
        List<RecipeIngredient> ingredients = List.of(new RecipeIngredient());

        Step mappedStep = new Step();
        mappedStep.setInstructions("Mix flour");

        Recipe mappedRecipe = new Recipe();
        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(123L);

        RecipeSummaryResponse expectedResponse = createSummaryResponse(123L);

        when(recipeMapper.toEntity(request)).thenReturn(mappedRecipe);
        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(author);
        when(slugService.generate(request.name())).thenReturn(generatedSlug);
        when(categoryService.findCategoryById(categoryId)).thenReturn(category);
        when(tagService.findTagsByIds(tagIds)).thenReturn(tags);
        when(recipeIngredientService.assembleIngredients(anyList())).thenReturn(ingredients);
        when(stepMapper.toEntity(any(StepAppendRequest.class))).thenReturn(mappedStep);
        when(recipeRepository.save(mappedRecipe)).thenReturn(savedRecipe);
        when(recipeMapper.toSummary(savedRecipe)).thenReturn(expectedResponse);

        RecipeSummaryResponse actualResponse = recipeService.createRecipe(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        assertThat(mappedRecipe.getAuthor()).isEqualTo(author);
        assertThat(mappedRecipe.getSlug()).isEqualTo(generatedSlug);
        assertThat(mappedRecipe.getPublicationDate()).isNotNull();

        verify(recipeRepository).save(mappedRecipe);
    }

    @Test
    void createRecipe_WhenCategoryNotFound_ShouldThrowException() {
        Long invalidCategoryId = 999L;
        RecipeCreateRequest request = createValidRequest(invalidCategoryId, List.of());

        Recipe mappedRecipe = new Recipe();
        when(recipeMapper.toEntity(request)).thenReturn(mappedRecipe);
        when(userContext.getCurrentUserId()).thenReturn(1L);
        when(userService.findUserById(1L)).thenReturn(new User());
        when(slugService.generate(any())).thenReturn("slug");
        when(categoryService.findCategoryById(invalidCategoryId))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        assertThatThrownBy(() -> recipeService.createRecipe(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Category not found");

        verify(recipeRepository, never()).save(any());
    }

    @Test
    void createRecipe_WhenUserNotFound_ShouldThrowException() {
        RecipeCreateRequest request = createValidRequest(10L, List.of());
        Recipe mappedRecipe = new Recipe();

        when(recipeMapper.toEntity(request)).thenReturn(mappedRecipe);
        when(userContext.getCurrentUserId()).thenReturn(1L);

        when(userService.findUserById(1L))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThatThrownBy(() -> recipeService.createRecipe(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(recipeRepository, never()).save(any());
        verifyNoInteractions(categoryService, tagService);
    }

    private RecipeCreateRequest createValidRequest(Long categoryId, List<Long> tagIds) {
        return new RecipeCreateRequest(
                "Pancakes",
                "Description",
                15,
                4,
                "http://img.url",
                categoryId,
                tagIds,
                List.of(new RecipeIngredientAddRequest(1L, 100.0, "g")), // Ingredients
                List.of(new StepAppendRequest(1, "Mix", null)) // Steps
        );
    }

    private RecipeSummaryResponse createSummaryResponse(Long id) {
        return new RecipeSummaryResponse(
                id, "Pancakes", "slug", 15, "url", 0.0, 0, "Author", "Category", List.of()
        );
    }
}