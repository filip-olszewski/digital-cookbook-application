package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.annotation.IgnoreAuditFields;
import io.github.filipolszewski.cookbook.dto.auth.SignupRequest;
import io.github.filipolszewski.cookbook.dto.user.UserPrivateProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserPublicProfileResponse;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {
    @IgnoreAuditFields
    @Mapping(target = "name.firstName", source = "firstName")
    @Mapping(target = "name.middleName", source = "middleName")
    @Mapping(target = "name.lastName", source = "lastName")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    User toEntity(SignupRequest request);

    @Mapping(target = "fullName", source = "user.name.fullName")
    UserSummaryResponse toSummary(User user);

    @Mapping(target = "fullName", source = "user.name.fullName")
    @Mapping(target = "joinedAt", source = "user.createdAt")
    UserPublicProfileResponse toPublicProfile(User user, long recipeCount);

    @Mapping(target = "fullName", source = "user.name.fullName")
    @Mapping(target = "joinedAt", source = "user.createdAt")
    UserPrivateProfileResponse toPrivateProfile(User user, long recipeCount, long reviewCount, long favouriteCount);
}
