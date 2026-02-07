package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.review.ReviewPostRequest;
import io.github.filipolszewski.cookbook.dto.review.ReviewResponse;
import io.github.filipolszewski.cookbook.dto.step.StepResponse;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.model.entity.Step;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {
        UserMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {
    Review toEntity(ReviewPostRequest request);

    @Mapping(target = "postedAt", source = "createdAt")
    ReviewResponse toResponse(Review review);
}
