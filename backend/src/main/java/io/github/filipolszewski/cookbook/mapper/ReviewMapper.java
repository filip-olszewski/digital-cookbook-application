package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.annotation.IgnoreAuditFields;
import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.review.ReviewUpdateRequest;
import io.github.filipolszewski.cookbook.dto.step.StepResponse;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.model.entity.Step;
import org.mapstruct.*;
import org.springframework.context.annotation.Bean;

@Mapper(
    config = CentralMapperConfig.class,
    uses = {
        UserMapper.class,
        JsonNullableMapper.class
    }
)
public interface ReviewMapper {
    @IgnoreAuditFields
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    Review toEntity(ReviewPostRequest request);

    @Mapping(target = "postedAt", source = "createdAt")
    ReviewResponse toResponse(Review review);

    @IgnoreAuditFields
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Review review, ReviewUpdateRequest request);
}
