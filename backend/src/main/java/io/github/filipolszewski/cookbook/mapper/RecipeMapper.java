package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.annotation.IgnoreAuditFields;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeCreateRequest;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.*;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(
        config = CentralMapperConfig.class,
        uses = {
                CategoryMapper.class,
                TagMapper.class,
                UserMapper.class,
                StepMapper.class,
                RecipeIngredientMapper.class,
                JsonNullableMapper.class
        }
)
public interface RecipeMapper {

    @Mapping(target = "ingredients", source = "recipeIngredients")
    @Mapping(target = "rating", source = "averageRating")
    @Mapping(target = "favouriteCount", ignore = true)
    RecipeDetailsResponse toDetails(Recipe recipe);

    @Mapping(target = "authorName", source = "author.name.fullName", defaultValue = "Unknown")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTagLabels")
    @Mapping(target = "rating", source = "averageRating")
    @Mapping(target = "category", source = "category.name")
    RecipeSummaryResponse toSummary(Recipe recipe);

    @IgnoreAuditFields
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "author", ignore = true)
    Recipe toEntity(RecipeCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @IgnoreAuditFields
    @Mapping(target = "name", conditionQualifiedByName = "notBlank")
    @Mapping(target = "description", conditionQualifiedByName = "notBlank")
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "recipeIngredients", ignore = true)
    @Mapping(target = "steps", ignore = true)
    void update(@MappingTarget Recipe recipe, RecipeUpdateRequest request);

    @Named("mapTagLabels")
    default List<String> mapTagLabels(Set<Tag> tags) {
        if(tags == null) return Collections.emptyList();
        return tags.stream().map(Tag::getLabel).sorted().toList();
    }
}
