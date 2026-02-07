package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.auth.SignupRequest;
import io.github.filipolszewski.cookbook.dto.user.UserPublicProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(target = "name.firstName", source = "firstName")
    @Mapping(target = "name.middleName", source = "middleName")
    @Mapping(target = "name.lastName", source = "lastName")
    User toEntity(SignupRequest request);

    @Mapping(target = "fullName", source = "name.fullName")
    UserSummaryResponse toSummary(User user);

    @Mapping(target = "fullName", source = "user.name.fullName")
    @Mapping(target = "joinedAt", source = "user.createdAt")
    @Mapping(target = "recipeCount", source = "recipeCount")
    UserPublicProfileResponse toPublicDetails(User user, int recipeCount);
}
