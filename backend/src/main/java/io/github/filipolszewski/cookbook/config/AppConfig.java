package io.github.filipolszewski.cookbook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Slugify slugify() {
        return Slugify.builder().build();
    }

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
