package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagUpdateRequest;
import io.github.filipolszewski.cookbook.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/tags")
@Tag(name = "Tags", description = "Endpoints for managing recipe tags")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Get all tags", description = "Public endpoint to retrieve a list of all tags.")
    @GetMapping
    public ResponseEntity<List<TagResponse>> getTags() {
        return ResponseEntity.ok(tagService.getTags());
    }

    @Operation(
        summary = "Create a new tag",
        description = "Admin only. Requires a valid JWT with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<TagResponse> addTag(@RequestBody @Valid TagCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.addTag(request));
    }

    @Operation(
        summary = "Delete a tag",
        description = "Admin only. Requires a valid JWT with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Update a tag",
        description = "Admin only. Requires a valid JWT with ADMIN authority.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PatchMapping("/{id}")
    public ResponseEntity<TagResponse> updateTag(
            @PathVariable Long id,
            @RequestBody @Valid TagUpdateRequest request
    ) {
        return ResponseEntity.ok(tagService.updateTag(id, request));
    }
}