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

    private final Slugify slugify = Slugify.builder().build();

    public List<CategorySummaryResponse> getCategories(CategorySearchCriteria criteria) {
        Specification<Category> spec = specificationBuilder.build(criteria);
        return categoryRepository.findAll(spec).stream()
                .map(categoryMapper::toSummary)
                .toList();
    }

    public CategoryDetailsResponse getCategory(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(categoryMapper::toDetails)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "slug", slug)));
    }

    @Transactional
    public CategorySummaryResponse createCategory(CategoryCreateRequest request) {
        String slug = slugify.slugify(request.name());
        verifyCategorySlugUniqueness(slug);

        Category category = categoryMapper.toEntity(request);
        category.setSlug(slug);

        if(request.parentId() != null) {
            Category parent = findCategoryById(request.parentId());
            category.moveTo(parent);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.toSummary(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        // If either has subcategories or recipes belonging to it throw an error
        if (categoryRepository.existsByParentCategoryId(id) || recipeRepository.existsByCategoryId(id)) {
            throw new ResourceConflictException(
                "Cannot delete category that has sub-categories or recipes assigned.");
        }

        categoryRepository.delete(findCategoryById(id));
    }

    @Transactional
    public CategoryDetailsResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = findCategoryById(id);
        categoryMapper.updateBasicFields(category, request);

        if (request.name() != null &&
           !request.name().isBlank() &&
           !request.name().equals(category.getName())) {

            category.setName(request.name());

            String newSlug = slugify.slugify(request.name());
            if (!newSlug.equals(category.getSlug())) {
                verifyCategorySlugUniqueness(newSlug);
                category.setSlug(newSlug);
            }
        }

        if (request.parentId().isPresent() &&
           !request.parentId().get().equals(category.getParentCategory().getId())) {

            Long parentId = request.parentId().get();
            category.moveTo(parentId == null ? null : findCategoryById(parentId));
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDetails(savedCategory);
    }


    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Category.class, "id", id)));
    }

    private void verifyCategorySlugUniqueness(String slug) {
        if(categoryRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Category.class, "slug", slug));
        }
    }
}
