package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.recipe.RecipeDetailsResponse;
import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {
    CategoryMapper.class, TagMapper.class, UserMapper.class,
    StepMapper.class, RecipeIngredientMapper.class
})
public interface RecipeMapper {

    @Mapping(target = "ingredients", source = "recipeIngredients")
    @Mapping(target = "rating", source = "averageRating")
    @Mapping(target = "favouriteCount", source = "favourites", qualifiedByName = "calculateTotalLikes")
    RecipeDetailsResponse toDetails(Recipe recipe);

    @Mapping(target = "authorName", source = "author.name.fullName", defaultValue = "Unknown")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "mapTagLabels")
    @Mapping(target = "rating", source = "averageRating")
    @Mapping(target = "category", source = "category.name")
    RecipeSummaryResponse toSummary(Recipe recipe);

    @Named("calculateTotalLikes")
    default Integer calculateTotalLikes(Set<Favourite> favourites) {
        if(favourites == null) return 0;
        return favourites.size();
    }

    @Named("mapTagLabels")
    default List<String> mapTagLabels(Set<Tag> tags) {
        if(tags == null) return Collections.emptyList();
        return tags.stream().map(Tag::getLabel).sorted().toList();
    }
}
