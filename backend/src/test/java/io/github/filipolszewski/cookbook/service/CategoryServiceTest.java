package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.category.CategoryCreateRequest;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.category.CategoryUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceConflictException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.CategoryMapper;
import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.repository.CategoryRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.specification.SpecificationBuilder;
import io.github.filipolszewski.cookbook.specification.criteria.CategorySearchCriteria;
import io.github.filipolszewski.cookbook.util.SlugUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private SpecificationBuilder<Category, CategorySearchCriteria> specificationBuilder;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @SuppressWarnings("unchecked")
    void getCategories_WhenCriteriaProvided_ShouldReturnMappedList() {
        Long categoryId = 1L;
        String name = "dinner";
        String slug = "dinner";

        var criteria = new CategorySearchCriteria("din", null, true);
        Specification<Category> specification = (Specification<Category>) mock(Specification.class);

        var category = new Category();
        category.setName("dinner");

        var dto = new CategorySummaryResponse(
            categoryId, name, slug, null
        );

        var expected = List.of(dto);

        when(specificationBuilder.build(criteria)).thenReturn(specification);
        when(categoryRepository.findAll(specification)).thenReturn(List.of(category));
        when(categoryMapper.toSummary(category)).thenReturn(dto);

        List<CategorySummaryResponse> res = categoryService.getCategories(criteria);

        assertEquals(expected, res);
        verify(specificationBuilder).build(eq(criteria));
        verify(categoryRepository).findAll(eq(specification));
        verify(categoryMapper).toSummary(eq(category));
    }

    @Test
    void getCategory_WhenCategoryExists_ShouldReturnCategoryDetails() {
        String slug = "dinner";

        var entity = new Category();
        entity.setId(1L);
        entity.setSlug("dinner");

        var expected = new CategoryDetailsResponse(1L, "", "dinner", "", null);

        when(categoryRepository.findBySlug(slug)).thenReturn(Optional.of(entity));
        when(categoryMapper.toDetails(entity)).thenReturn(expected);

        CategoryDetailsResponse res = categoryService.getCategory(slug);

        assertNotNull(res);
        assertEquals(expected.slug(), res.slug());
        assertEquals(expected.id(), res.id());

        verify(categoryRepository).findBySlug(eq(slug));
        verify(categoryMapper).toDetails(eq(entity));
    }

    @Test
    void getCategory_WhenCategoryNotFound_ShouldThrowResourceNotFoundException() {
        String slug = "slug";
        when(categoryRepository.findBySlug(slug)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategory(slug);
        });

        assertEquals("Category with slug ['slug'] not found.", ex.getMessage());
        verify(categoryRepository).findBySlug(slug);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void createCategory_WhenParentIdIsValid_ShouldSaveSubCategory() {
        String name = "dinner";
        String slug = "dinner";
        Long parentId = 5L;
        Long categoryId = 10L;

        var request = new CategoryCreateRequest(name, null, parentId);

        var mappedCategory = new Category();
        mappedCategory.setName(name);
        mappedCategory.setSlug(slug);

        var parentCategory = new Category();
        parentCategory.setId(parentId);

        var savedCategory = new Category();
        savedCategory.setId(categoryId);
        savedCategory.setName(name);
        savedCategory.setSlug(slug);
        savedCategory.setParentCategory(parentCategory);

        var expected = new CategorySummaryResponse(categoryId, name, slug, parentId);

        when(categoryRepository.existsBySlug(slug)).thenReturn(false);
        when(categoryMapper.toEntity(request, slug)).thenReturn(mappedCategory);
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(mappedCategory)).thenReturn(savedCategory);
        when(categoryMapper.toSummary(savedCategory)).thenReturn(expected);

        CategorySummaryResponse res = categoryService.createCategory(request);

        assertEquals(expected, res);
        assertEquals(mappedCategory.getParentCategory(), parentCategory);

        verify(categoryRepository).save(mappedCategory);
        verify(categoryRepository).findById(parentId);
    }

    @Test
    void createCategory_WhenParentIdIsNull_ShouldSaveRootCategory() {
        String slug = "dinner";
        String name = "dinner";
        Long categoryId = 1L;

        var request = new CategoryCreateRequest(name, null, null);

        var mappedCategory = new Category();
        mappedCategory.setName(name);
        mappedCategory.setSlug(slug);

        var savedCategory = new Category();
        savedCategory.setId(categoryId);
        savedCategory.setName(name);
        savedCategory.setSlug(slug);
        savedCategory.setParentCategory(null);

        var expected = new CategorySummaryResponse(categoryId, name, slug, null);

        when(categoryRepository.existsBySlug(slug)).thenReturn(false);
        when(categoryMapper.toEntity(request, slug)).thenReturn(mappedCategory);
        when(categoryRepository.save(mappedCategory)).thenReturn(savedCategory);
        when(categoryMapper.toSummary(savedCategory)).thenReturn(expected);

        CategorySummaryResponse res = categoryService.createCategory(request);

        assertEquals(expected, res);
        verify(categoryRepository, never()).findById(any());
        verify(categoryRepository).save(mappedCategory);
    }

    @Test
    void createCategory_WhenSlugAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        var request = new CategoryCreateRequest("existing category", null, null);
        String slug = "existing-category";

        when(categoryRepository.existsBySlug(slug)).thenReturn(true);
        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            categoryService.createCategory(request);
        });

        assertEquals("Category with this slug ['existing-category'] already exists.",
                ex.getMessage());
        verify(categoryRepository).existsBySlug(slug);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldDelete() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.existsByParentCategoryId(categoryId)).thenReturn(false);
        when(recipeRepository.existsByCategoryId(categoryId)).thenReturn(false);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        assertEquals("Category with id ['1'] not found.", ex.getMessage());

        verify(categoryRepository, never()).existsByParentCategoryId(any());
        verify(recipeRepository, never()).existsByCategoryId(any());
        verify(categoryRepository, never()).deleteById(categoryId);
    }

    @Test
    void deleteCategory_WhenCategoryHasSubCategories_ShouldThrowResourceConflictException() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.existsByParentCategoryId(categoryId)).thenReturn(true);

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        assertEquals("Cannot delete category that has sub-categories or recipes assigned.",
                ex.getMessage());

        verify(categoryRepository).existsById(eq(categoryId));
        verify(categoryRepository).existsByParentCategoryId(eq(categoryId));
        verify(recipeRepository, never()).existsByCategoryId(eq(categoryId));
        verify(categoryRepository, never()).deleteById(eq(categoryId));
    }

    @Test
    void deleteCategory_WhenCategoryHasAssignedRecipes_ShouldThrowResourceConflictException() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.existsByParentCategoryId(categoryId)).thenReturn(false);
        when(recipeRepository.existsByCategoryId(categoryId)).thenReturn(true);

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            categoryService.deleteCategory(categoryId);
        });

        assertEquals("Cannot delete category that has sub-categories or recipes assigned.",
                ex.getMessage());

        verify(categoryRepository).existsById(eq(categoryId));
        verify(categoryRepository).existsByParentCategoryId(eq(categoryId));
        verify(recipeRepository).existsByCategoryId(eq(categoryId));
        verify(categoryRepository, never()).deleteById(eq(categoryId));
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long categoryId = 1L;
        var request = new CategoryUpdateRequest(
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.undefined()
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertEquals("Category with id ['1'] not found.", ex.getMessage());

        verify(categoryRepository).findById(eq(categoryId));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNameChanged_ShouldUpdateNameAndSlug() {
        Long categoryId = 1L;
        String oldName = "supper";
        String oldSlug = "supper";
        String newName = "dinner";
        String newSlug = "dinner";

        var request = new CategoryUpdateRequest(
                JsonNullable.of(newName), JsonNullable.undefined(), JsonNullable.undefined()
        );

        var foundCategory = new Category();
        foundCategory.setId(categoryId);
        foundCategory.setName(oldName);
        foundCategory.setSlug(oldSlug);

        var savedCategory = new Category();
        savedCategory.setId(categoryId);
        savedCategory.setName(newName);
        savedCategory.setSlug(newSlug);

        var expected = new CategoryDetailsResponse(
            categoryId, newName, newSlug, null, null
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foundCategory));
        when(categoryRepository.existsBySlug(newSlug)).thenReturn(false);
        when(categoryRepository.save(foundCategory)).thenReturn(savedCategory);
        when(categoryMapper.toDetails(savedCategory)).thenReturn(expected);

        CategoryDetailsResponse res = categoryService.updateCategory(categoryId, request);

        assertEquals(expected, res);
        assertEquals(newName, foundCategory.getName());
        assertEquals(newSlug, foundCategory.getSlug());

        verify(categoryRepository).save(eq(foundCategory));
    }

    @Test
    void updateCategory_WhenSlugDuplicate_ShouldThrowResourceAlreadyExistsException() {
        Long categoryId = 1L;
        String duplicateName = "dinner";
        String duplicateSlug = "dinner";

        var request = new CategoryUpdateRequest(
            JsonNullable.of(duplicateName), JsonNullable.undefined(), JsonNullable.undefined()
        );

        var foundCategory = new Category();
        foundCategory.setId(categoryId);
        foundCategory.setName("meal");
        foundCategory.setSlug("meal");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foundCategory));
        when(categoryRepository.existsBySlug(duplicateSlug)).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertEquals("Category with this slug ['dinner'] already exists.", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNewParentIsDescendant_ShouldThrowResourceConflictException() {
        Long oldParentId = 1L;
        Long categoryId = 2L;
        Long newParentId = 3L;

        var request = new CategoryUpdateRequest(
            JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.of(newParentId)
        );

        var oldParentCategory = new Category();
        oldParentCategory.setId(oldParentId);

        var foundCategory = new Category();
        foundCategory.setId(categoryId);
        foundCategory.setParentCategory(oldParentCategory);

        var newParentCategory = new Category();
        newParentCategory.setId(newParentId);
        newParentCategory.setParentCategory(foundCategory);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foundCategory));
        when(categoryRepository.findById(newParentId)).thenReturn(Optional.of(newParentCategory));

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertEquals("Cannot move a category into its own sub-category.", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNewParentIsSelf_ShouldThrowResourceConflictException() {
        Long categoryId = 1L;

        var request = new CategoryUpdateRequest(
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.of(categoryId)
        );

        var foundCategory = new Category();
        foundCategory.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foundCategory));

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertEquals("Category cannot be its own parent.", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenNewParentDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long categoryId = 1L;
        Long invalidParentId = 999L;

        var request = new CategoryUpdateRequest(
                JsonNullable.undefined(), JsonNullable.undefined(), JsonNullable.of(invalidParentId)
        );

        var foundCategory = new Category();
        foundCategory.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foundCategory));
        when(categoryRepository.findById(invalidParentId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory(categoryId, request);
        });

        assertEquals("Category with id ['999'] not found.", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_WhenAllFieldsValid_ShouldUpdateCategory() {
        Long categoryId = 1L;
        Long newParentId = 2L;
        String oldName = "meal";
        String oldSlug = "meal";
        String newName = "dinner";
        String newSlug = "dinner";
        String newImgUrl = "image";

        var request = new CategoryUpdateRequest(
            JsonNullable.of(newName), JsonNullable.of(newImgUrl), JsonNullable.of(newParentId)
        );

        var foundCategory = new Category();
        foundCategory.setId(categoryId);
        foundCategory.setName(oldName);
        foundCategory.setSlug(oldSlug);

        var newParent = new Category();
        newParent.setId(newParentId);

        var savedCategory = new Category();
        savedCategory.setId(categoryId);
        savedCategory.setName(newName);
        savedCategory.setSlug(newSlug);
        savedCategory.setImgUrl(newImgUrl);
        savedCategory.setParentCategory(newParent);

        var expected = new CategoryDetailsResponse(
            categoryId, newName, newSlug, newImgUrl, new CategorySummaryResponse(
                newParentId, "any", "any", null
            )
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(foundCategory));
        when(categoryRepository.existsBySlug(newSlug)).thenReturn(false);
        when(categoryRepository.findById(newParentId)).thenReturn(Optional.of(newParent));
        when(categoryRepository.save(foundCategory)).thenReturn(savedCategory);
        when(categoryMapper.toDetails(savedCategory)).thenReturn(expected);

        CategoryDetailsResponse res = categoryService.updateCategory(categoryId, request);

        assertEquals(expected, res);
        assertEquals(newName, foundCategory.getName());
        assertEquals(newSlug, foundCategory.getSlug());
        assertEquals(newImgUrl, foundCategory.getImgUrl());
        assertEquals(newParent, foundCategory.getParentCategory());

        verify(categoryRepository).save(eq(foundCategory));
    }
}