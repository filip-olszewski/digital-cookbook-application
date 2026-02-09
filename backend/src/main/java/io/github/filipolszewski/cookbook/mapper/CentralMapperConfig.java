package io.github.filipolszewski.cookbook.mapper;

import org.mapstruct.Condition;
import org.mapstruct.MapperConfig;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {CommonConditions.class}
)
public interface CentralMapperConfig {
}
