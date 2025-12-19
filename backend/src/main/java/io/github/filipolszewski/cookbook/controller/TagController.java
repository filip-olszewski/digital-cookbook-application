package io.github.filipolszewski.cookbook.controller;

import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstants.API_V1 + "/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    ResponseEntity<List<TagResponse>> getTags() {
        return ResponseEntity.ok(tagService.getTags());
    }

}
