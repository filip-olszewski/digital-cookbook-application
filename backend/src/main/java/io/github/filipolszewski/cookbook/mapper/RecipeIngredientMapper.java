package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientResponse;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.RecipeIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeIngredientMapper {
    @Mapping(target = "name", source = "ingredient.name")
    @Mapping(target = "type", source = "ingredient.type")
    RecipeIngredientResponse toResponse(RecipeIngredient recipeIngredient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    RecipeIngredient toEntity(RecipeIngredientAddRequest request);
}
