package io.github.filipolszewski.cookbook.dto.file;

public record UploadUrlResponse(
    String url,
    String imageKey
) {
}
