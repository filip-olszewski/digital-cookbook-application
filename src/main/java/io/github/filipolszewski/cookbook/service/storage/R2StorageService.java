package io.github.filipolszewski.cookbook.service.impl;

import io.github.filipolszewski.cookbook.config.properties.R2Properties;
import io.github.filipolszewski.cookbook.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class R2StorageService implements StorageService {

    private final S3Presigner presigner;
    private final R2Properties properties;

    @Override
    public String generateUploadUrl(String key, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .key(key)
                .bucket(properties.bucket())
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(request)
                .build();

        return presigner.presignPutObject(presignRequest).url().toString();
    }

    @Override
    public String generateDownloadUrl(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .key(key)
                .bucket(properties.bucket())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(request)
                .signatureDuration(Duration.ofMinutes(30))
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }
}
