package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.category.CategoryDetailsResponse;
import io.github.filipolszewski.cookbook.dto.category.CategorySummaryResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagUpdateRequest;
import io.github.filipolszewski.cookbook.model.entity.Category;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TagMapper {
    TagResponse toResponse(Tag tag);
    Tag toEntity(TagCreateRequest request);
}
