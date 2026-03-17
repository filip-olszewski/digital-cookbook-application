package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.file.DownloadUrlResponse;
import io.github.filipolszewski.cookbook.dto.file.UploadUrlResponse;
import io.github.filipolszewski.cookbook.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final StorageService storageService;
    private final UserContext userContext;

    public UploadUrlResponse getPresignedUploadUrl(String contentType) {
        String key = buildKey();
        String url = storageService.generateUploadUrl(key, contentType);
        return new UploadUrlResponse(url, key);
    }

    public DownloadUrlResponse getPresignedDownloadUrl(String key) {
        String url = storageService.generateDownloadUrl(key);
        return new DownloadUrlResponse(url);
    }

    private String buildKey() {
        return String.format(
                "recipes/%d/%s",
                userContext.getCurrentUserId(),
                UUID.randomUUID()
        );
    }

}
