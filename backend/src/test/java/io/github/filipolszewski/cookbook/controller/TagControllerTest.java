package io.github.filipolszewski.cookbook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import io.github.filipolszewski.cookbook.service.TagService;
import io.github.filipolszewski.cookbook.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@WithMockUser(roles = "USER")
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTags_WhenTagsExist_ShouldReturnAllTags() throws Exception {
        List<TagResponse> tags = List.of(
            new TagResponse(1L, "Vegan"),
            new TagResponse(2L, "Spicy")
        );

        when(tagService.getTags()).thenReturn(tags);

        mockMvc.perform(get("/api/v1/tags").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].label").value("Vegan"))
                .andExpect(jsonPath("$[1].label").value("Spicy"));
    }

    @Test
    void getTags_WhenNoTags_ShouldReturnEmptyList() throws Exception {
        when(tagService.getTags()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/tags").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addTag_WhenTagLabelUnique_ShouldSaveAndReturnTag() throws Exception {
        TagCreateRequest request = new TagCreateRequest("Vegan");
        TagResponse res = new TagResponse(1L, "Vegan");

        when(tagService.addTag(request)).thenReturn(res);

        mockMvc.perform(post("/api/v1/tags").contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.label").value("Vegan"));
    }

}
