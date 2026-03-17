package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.file.DownloadUrlResponse;
import io.github.filipolszewski.cookbook.dto.file.UploadUrlResponse;
import io.github.filipolszewski.cookbook.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/media")

public class MediaController {

    private final MediaService service;

    @Operation(
        summary = "Generate Presigned Upload URL",
        description = "Secured endpoint to retrieve presigned URL to upload images directly to object storage.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("/upload-url")
    public UploadUrlResponse getPresignedUploadUrl(
        @RequestParam String contentType
    ) {
        return service.getPresignedUploadUrl(contentType);
    }

    @Operation(
        summary = "Generate Presigned Download URL",
        description = "Public endpoint to retrieve a presigned url to download images directly from object storage."
    )
    @GetMapping("/download-url")
    public DownloadUrlResponse getPresignedDownloadUrl(
        @RequestParam String imageKey
    ) {
        return service.getPresignedDownloadUrl(imageKey);
    }
}
