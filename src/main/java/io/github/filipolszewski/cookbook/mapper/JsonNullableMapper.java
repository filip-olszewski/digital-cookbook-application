package io.github.filipolszewski.cookbook.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring")
public interface JsonNullableMapper {

    @Condition
    default <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }

    default <T> T unwrap(JsonNullable<T> nullable) {
        return nullable.orElse(null);
    }

    default <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }

}
