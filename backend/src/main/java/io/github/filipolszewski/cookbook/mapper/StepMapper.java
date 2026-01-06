package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import io.github.filipolszewski.cookbook.dto.step.StepResponse;
import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Step;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StepMapper {
    StepResponse toResponse(Step step);

    @Mapping(target = "imgUrl", source = "request.imgUrl")
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "id", ignore = true)
    Step toEntity(StepAppendRequest request);
}
