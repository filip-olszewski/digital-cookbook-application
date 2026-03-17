package io.github.filipolszewski.cookbook.service;

public interface StorageService {
    String generateUploadUrl(String key, String contentType);
    String generateDownloadUrl(String key);
}
