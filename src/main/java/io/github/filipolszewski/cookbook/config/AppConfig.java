package io.github.filipolszewski.cookbook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.slugify.Slugify;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Cookbook API")
                        .version("1.0")
                        .description("REST API for managing and reviewing recipes.")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Filip Olszewski")
                                .url("https://github.com/filip-olszewski")
                                .email("olszewski.fp@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .name("Bearer Authentication")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
