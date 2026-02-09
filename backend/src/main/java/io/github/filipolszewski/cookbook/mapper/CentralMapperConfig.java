package io.github.filipolszewski.cookbook.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CentralMapperConfig {
    @Named("notBlank")
    default Boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
