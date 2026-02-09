package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.annotation.IgnoreAuditFields;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import org.mapstruct.*;

@Mapper(config = CentralMapperConfig.class)
public interface IngredientMapper {

    @IgnoreAuditFields
    Ingredient toEntity(IngredientCreateRequest request);

    IngredientSummaryResponse toSummary(Ingredient ingredient);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @IgnoreAuditFields
    @Mapping(target = "name", ignore = true) // Handle uniqueness manually
    void update(@MappingTarget Ingredient ingredient, IngredientUpdateRequest request);
}
