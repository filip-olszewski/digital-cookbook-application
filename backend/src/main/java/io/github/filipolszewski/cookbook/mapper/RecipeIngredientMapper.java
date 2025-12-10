package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientResponse;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.RecipeIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecipeIngredientMapper {
    @Mapping(target = "name", source = "ingredient.name")
    @Mapping(target = "type", source = "ingredient.type")
    RecipeIngredientResponse toResponse(RecipeIngredient recipeIngredient);
}
