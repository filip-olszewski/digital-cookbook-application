package io.github.filipolszewski.cookbook.mapper;

import io.github.filipolszewski.cookbook.dto.user.UserCreateRequest;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "name.firstName", source = "firstName")
    @Mapping(target = "name.middleName", source = "middleName")
    @Mapping(target = "name.lastName", source = "lastName")
    @Mapping(target = "role", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "firstName", source = "name.firstName")
    @Mapping(target = "middleName", source = "name.middleName")
    @Mapping(target = "lastName", source = "name.lastName")
    UserSummaryResponse toSummary(User user);
}
