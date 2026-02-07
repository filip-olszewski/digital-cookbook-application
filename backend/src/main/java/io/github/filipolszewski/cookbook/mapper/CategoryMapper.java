package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.category.CategoryCreateRequest;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.category.CategoryUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.Category;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {
        JsonNullableMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parentCategory.id")
    CategorySummaryResponse toSummary(Category category);

    @Mapping(target = "parent", source = "parentCategory")
    CategoryDetailsResponse toDetails(Category category);

    Category toEntity(CategoryCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "name", ignore = true)
    void update(@MappingTarget Category category, CategoryUpdateRequest request);
}