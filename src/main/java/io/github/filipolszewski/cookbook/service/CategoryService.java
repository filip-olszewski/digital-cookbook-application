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
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.repository.CategoryRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.specification.SpecificationBuilder;
import io.github.filipolszewski.cookbook.specification.criteria.CategorySearchCriteria;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import io.github.filipolszewski.cookbook.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final SpecificationBuilder<Category, CategorySearchCriteria> specificationBuilder;

    private final RecipeRepository recipeRepository;

    private final Slugify slugify;

    public List<CategorySummaryResponse> getCategories(CategorySearchCriteria criteria) {
        log.debug("Fetching categories based on search criteria");
        Specification<Category> spec = specificationBuilder.build(criteria);
        return categoryRepository.findAll(spec).stream()
                .map(categoryMapper::toSummary)
                .toList();
    }

    public CategoryDetailsResponse getCategory(String slug) {
        log.debug("Fetching category details for slug: {}", slug);
        return categoryRepository.findBySlugWithParent(slug)
                .map(categoryMapper::toDetails)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "slug", slug)));
    }

    @Transactional
    public CategorySummaryResponse createCategory(CategoryCreateRequest request) {
        String slug = slugify.slugify(request.name());
        log.info("Creating new category with name: {}, mapped to slug: {}", request.name(), slug);

        // Does the slug already exist?
        if(categoryRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Category.class, "slug", slug));
        }

        Category category = categoryMapper.toEntity(request);
        category.setSlug(slug);

        if(request.parentId() != null) {
            log.debug("Assigning new category to parent ID: {}", request.parentId());
            Category parent = findCategoryByIdWithSubcategories(request.parentId());
            moveCategory(category, parent);
        }

        Category saved = categoryRepository.save(category);
        log.info("Successfully created category with ID: {}", saved.getId());

        return categoryMapper.toSummary(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        log.info("Attempting to delete category with ID: {}", id);

        // If either has subcategories or recipes belonging to it throw an error
        if (categoryRepository.existsByParentCategoryId(id) || recipeRepository.existsByCategoryId(id)) {
            throw new ResourceConflictException(
                    "Cannot delete category that has sub-categories or recipes assigned.");
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));
        categoryRepository.delete(category);

        log.info("Successfully deleted category with ID: {}", id);
    }

    @Transactional
    public CategoryDetailsResponse updateCategory(Long id, CategoryUpdateRequest request) {
        log.info("Updating category with ID: {}", id);

        // Fetch category along with its parent
        Category category = categoryRepository.findByIdWithParent(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));

        // Update basic fields if provided
        categoryMapper.update(category, request);

        if (request.parentId().isPresent()) {
            handleParentCategoryUpdate(category, request.parentId().get());
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Successfully updated category with ID: {}", savedCategory.getId());

        return categoryMapper.toDetails(savedCategory);
    }

    private void handleParentCategoryUpdate(Category category, Long newParentId) {
        Long currentParentId = (category.getParentCategory() != null)
                ? category.getParentCategory().getId()
                : null;

        // Is the new parent same as the old parent?
        if (!Objects.equals(newParentId, currentParentId)) {
            log.info("Category ID: {} is changing parent from ID: {} to ID: {}",
                    category.getId(), currentParentId, newParentId);

            // If explicitly null, set it to null (make it a root category)
            if (newParentId == null) {
                category.setParentCategory(null);
            }
            // Fetch parent category along with its subcategories and move current category into the new parent
            else {
                Category parent = findCategoryByIdWithSubcategories(newParentId);
                moveCategory(category, parent);
            }
        }
    }

    private void moveCategory(Category category, Category newParent) {
        if(category.getId().equals(newParent.getId())) {
            throw new ResourceConflictException("Category cannot be its own parent.");
        }

        if (isCycle(category, newParent)) {
            throw new ResourceConflictException("Cannot move a category into its own sub-category.");
        }

        category.setParentCategory(newParent);
        newParent.getSubCategories().add(category);
    }

    /**
     * Checks whether category's new parent is its own subcategory. If true, ends up with infinite cycle
     * of parent category and its children. Verifies that does not happen.
     * Usually depth of categories should not exceed 4 or 5, so the expense of iterations is manageable.
     * @param target        The category which we try to move under a new parent.
     * @param newParent     The new parent category we try to set on the target category.
     * @return              Whether the move results in a cycle.
     */
    private boolean isCycle(Category target, Category newParent) {
        Category current = newParent;
        while (current != null) {
            if (current.getId().equals(target.getId())) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));
    }

    private Category findCategoryByIdWithSubcategories(Long id) {
        return categoryRepository.findByIdWithSubcategories(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));
    }
}