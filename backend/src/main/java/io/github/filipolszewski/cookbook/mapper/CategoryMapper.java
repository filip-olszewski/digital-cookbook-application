package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.category.CategoryCreateRequest;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.category.CategoryUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.Category;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parentCategory.id")
    CategorySummaryResponse toSummary(Category category);

    @Mapping(target = "parent", source = "parentCategory")
    CategoryDetailsResponse toDetails(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryCreateRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "imgUrl", source = "imgUrl")
    void updateBasicFields(@MappingTarget Category category, CategoryUpdateRequest request);
}