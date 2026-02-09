package io.github.filipolszewski.cookbook.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    long expirationSeconds,
    long refreshExpirationSeconds
) {}
