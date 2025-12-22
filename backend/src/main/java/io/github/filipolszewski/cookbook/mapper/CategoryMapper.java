package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.category.CategoryCreateRequest;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.model.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.jdbc.datasource.lookup.IsolationLevelDataSourceRouter;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parentCategory.id")
    CategorySummaryResponse toSummary(Category category);

    @Mapping(target = "parent", source = "parentCategory")
    CategoryDetailsResponse toDetails(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "imgUrl", source = "request.imgUrl")
    Category toEntity(CategoryCreateRequest request, String slug);
}
