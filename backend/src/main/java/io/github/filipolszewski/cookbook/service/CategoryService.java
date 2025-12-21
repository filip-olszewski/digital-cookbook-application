package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.CategoryMapper;
import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.repository.CategoryRepository;
import io.github.filipolszewski.cookbook.specification.SpecificationBuilder;
import io.github.filipolszewski.cookbook.specification.criteria.CategorySearchCriteria;
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
}
