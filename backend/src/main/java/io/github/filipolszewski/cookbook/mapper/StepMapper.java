package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.annotation.IgnoreAuditFields;
import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.step.StepAppendRequest;
import io.github.filipolszewski.cookbook.dto.step.StepResponse;
import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Step;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = CentralMapperConfig.class)
public interface StepMapper {
    @IgnoreAuditFields
    @Mapping(target = "recipe", ignore = true)
    Step toEntity(StepAppendRequest request);

    StepResponse toResponse(Step step);
}
