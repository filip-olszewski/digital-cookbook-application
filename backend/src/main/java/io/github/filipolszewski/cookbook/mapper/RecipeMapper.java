package io.github.filipolszewski.cookbook.mapper;

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
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
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
    RecipeDetailsResponse toDetails(Recipe recipe);

    @Mapping(target = "authorName", source = "author.name.fullName", defaultValue = "Unknown")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTagLabels")
    @Mapping(target = "rating", source = "averageRating")
    @Mapping(target = "category", source = "category.name")
    RecipeSummaryResponse toSummary(Recipe recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "recipeIngredients", ignore = true)
    @Mapping(target = "steps", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    Recipe toEntity(RecipeCreateRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "prepTime", source = "prepTime")
    @Mapping(target = "servings", source = "servings")
    @Mapping(target = "imgUrl", source = "imgUrl")
    void updateBasicFields(@MappingTarget Recipe recipe, RecipeUpdateRequest request);

    @Named("calculateTotalLikes")
    default Integer calculateTotalLikes(List<Favourite> favourites) {
        if(favourites == null) return 0;
        return favourites.size();
    }

    @Named("mapTagLabels")
    default List<String> mapTagLabels(Set<Tag> tags) {
        if(tags == null) return Collections.emptyList();
        return tags.stream().map(Tag::getLabel).sorted().toList();
    }
}
