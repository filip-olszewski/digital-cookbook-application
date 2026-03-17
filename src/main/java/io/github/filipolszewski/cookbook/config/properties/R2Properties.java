package io.github.filipolszewski.cookbook.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage.cloudflare.r2")
public record R2Properties(
    String accessKeyId,
    String secretAccessKey,
    String region,
    String accountId,
    String bucket
) {
}
