package io.github.filipolszewski.cookbook.config;

import io.github.filipolszewski.cookbook.config.properties.R2Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(R2Properties.class)
@RequiredArgsConstructor
public class R2StorageConfig {

    private final R2Properties properties;

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .endpointOverride(URI.create(endpoint()))
            .region(Region.of(properties.region()))
            .serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .credentialsProvider(credentialsProvider())
            .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .endpointOverride(URI.create(endpoint()))
            .region(Region.of(properties.region()))
                .serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
                )
            .credentialsProvider(credentialsProvider())
            .build();
    }

    @Bean
    public StaticCredentialsProvider credentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(
            properties.accessKeyId(),
            properties.secretAccessKey()
        ));
    }

    public String endpoint() {
        return String.format("https://%s.r2.cloudflarestorage.com", properties.accountId());
    }

}
