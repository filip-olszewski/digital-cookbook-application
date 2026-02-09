package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.annotation.IgnoreAuditFields;
import io.github.filipolszewski.cookbook.dto.category.CategoryCreateRequest;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.category.CategoryUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.Category;
import org.mapstruct.*;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {
        JsonNullableMapper.class
    }
)
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parentCategory.id")
    CategorySummaryResponse toSummary(Category category);

    @Mapping(target = "parent", source = "parentCategory")
    CategoryDetailsResponse toDetails(Category category);

    @IgnoreAuditFields
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CategoryCreateRequest request);

    @IgnoreAuditFields
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "name", conditionQualifiedByName = "notBlank")
    void update(@MappingTarget Category category, CategoryUpdateRequest request);
}