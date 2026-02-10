package io.github.filipolszewski.cookbook.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CommonConditions {
    @Named("notBlank")
    @Condition
    default Boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
