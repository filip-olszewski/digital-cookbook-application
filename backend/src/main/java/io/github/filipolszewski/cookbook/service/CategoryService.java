package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
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
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import io.github.filipolszewski.cookbook.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final SpecificationBuilder<Category, CategorySearchCriteria> specificationBuilder;

    private final RecipeRepository recipeRepository;

    public List<CategorySummaryResponse> getCategories(CategorySearchCriteria criteria) {
        Specification<Category> spec = specificationBuilder.build(criteria);
        return categoryRepository.findAll(spec).stream()
                .map(categoryMapper::toSummary)
                .toList();
    }

    public CategoryDetailsResponse getCategory(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toDetails)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Category with a slug of '" + slug + "' not found."));
    }

    @Transactional
    public CategorySummaryResponse createCategory(CategoryCreateRequest request) {

        // Build a slug based on provided name
        String slug = Slugify.builder().build().slugify(request.name());

        // Check if category with this slug already exists
        if(categoryRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException(
                ErrorMessageUtil.exists(Category.class, "slug", slug));
        }

        Category category = categoryMapper.toEntity(request, slug);

        // If subcategory -> set parent
        if(request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    ErrorMessageUtil.notFound(Category.class, "id", request.parentId())));
            category.setParentCategory(parent);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.toSummary(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if(!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorMessageUtil.notFound(Category.class, "id", id));
        }

        // If either has subcategories or recipes belonging to it throw an error
        boolean hasChildren = categoryRepository.existsByParentCategoryId(id);
        boolean hasRecipes = recipeRepository.existsByCategoryId(id);

        if(hasChildren || hasRecipes) {
            throw new ResourceConflictException(
                "Cannot delete category that has sub-categories or recipes assigned.");
        }

        categoryRepository.deleteById(id);
    }

    @Transactional
    public CategoryDetailsResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));

        if (request.name().isPresent()) {
            updateCategoryName(category, request.name().get());
        }

        if (request.imgUrl().isPresent()) {
            updateCategoryImage(category, request.imgUrl().get());
        }

        if (request.parentId().isPresent()) {
            updateCategoryParent(category, request.parentId().get());
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDetails(savedCategory);
    }

    private void updateCategoryName(Category category, String newName) {
        if (newName == null || newName.isBlank() || newName.equals(category.getName())) {
            return;
        }

        String slug = SlugUtil.slugify(newName);
        if (!slug.equals(category.getSlug()) && categoryRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Category.class, "slug", slug));
        }

        category.setSlug(slug);
        category.setName(newName);
    }

    private void updateCategoryImage(Category category, String imgUrl) {
        category.setImgUrl((imgUrl != null && !imgUrl.isBlank()) ? imgUrl : null);
    }

    private void updateCategoryParent(Category category, Long newParentId) {
        if (newParentId == null) {
            category.setParentCategory(null);
            return;
        }

        if (newParentId.equals(category.getId())) {
            throw new ResourceConflictException("Category cannot be its own parent.");
        }

        Category newParent = categoryRepository.findById(newParentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", newParentId)));

        // Prevent cycles
        if (isDescendant(category, newParent)) {
            throw new ResourceConflictException("Cannot move a category into its own sub-category.");
        }

        category.setParentCategory(newParent);
    }

    private boolean isDescendant(Category category, Category newParent) {
        Category temp = newParent;
        while(temp != null) {
            if(temp.getId().equals(category.getId())) {
                return true;
            }

            temp = temp.getParentCategory();
        }
        return false;
    }
}
