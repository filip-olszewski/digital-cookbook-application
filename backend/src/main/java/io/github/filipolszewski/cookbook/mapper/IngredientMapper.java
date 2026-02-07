package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IngredientMapper {
    Ingredient toEntity(IngredientCreateRequest request);
    IngredientSummaryResponse toSummary(Ingredient ingredient);
}
