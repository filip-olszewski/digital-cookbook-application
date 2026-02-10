package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.TagMapper;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import io.github.filipolszewski.cookbook.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    void getTags_ShouldReturnListOfTags() {
        Tag tag = new Tag();
        tag.setId(1L);
        TagResponse response = new TagResponse(1L, "Vegan");

        when(tagRepository.findAll()).thenReturn(List.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(response);

        List<TagResponse> result = tagService.getTags();

        assertEquals(1, result.size());
        assertEquals("Vegan", result.getFirst().label());
    }

    @Test
    void addTag_WhenLabelIsUnique_ShouldSaveAndReturnTag() {
        String label = "Gluten Free";
        TagCreateRequest request = new TagCreateRequest(label);

        Tag tagEntity = new Tag();
        tagEntity.setLabel(label);

        Tag savedTag = new Tag();
        savedTag.setId(1L);
        savedTag.setLabel(label);

        TagResponse expectedResponse = new TagResponse(1L, label);

        when(tagRepository.existsByLabel(label)).thenReturn(false);
        when(tagMapper.toEntity(request)).thenReturn(tagEntity);
        when(tagRepository.save(tagEntity)).thenReturn(savedTag);
        when(tagMapper.toResponse(savedTag)).thenReturn(expectedResponse);

        TagResponse result = tagService.addTag(request);

        assertNotNull(result);
        assertEquals(label, result.label());
        verify(tagRepository).save(tagEntity);
    }

    @Test
    void addTag_WhenLabelAlreadyExists_ShouldThrowResourceAlreadyExistsException() {
        String label = "Vegan";
        TagCreateRequest request = new TagCreateRequest(label);

        when(tagRepository.existsByLabel(label)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> tagService.addTag(request));

        verify(tagRepository, never()).save(any());
    }

    @Test
    void deleteTag_WhenIdExists_ShouldDelete() {
        Long id = 1L;
        Tag tag = new Tag();
        tag.setId(id);

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        tagService.deleteTag(id);

        verify(tagRepository).findById(id);
        verify(tagRepository).delete(tag);
    }

    @Test
    void deleteTag_WhenIdDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long id = 1L;

        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(id));

        verify(tagRepository).findById(id);
        verify(tagRepository, never()).delete(any());
    }

    @Test
    void updateTag_WhenLabelChangedAndUnique_ShouldUpdateTag() {
        Long id = 1L;
        String newLabel = "New Label";
        TagUpdateRequest request = new TagUpdateRequest(newLabel);

        Tag existingTag = new Tag();
        existingTag.setId(id);
        existingTag.setLabel("Old Label");

        Tag savedTag = new Tag();
        savedTag.setId(id);
        savedTag.setLabel(newLabel);

        TagResponse expectedResponse = new TagResponse(id, newLabel);

        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.existsByLabel(newLabel)).thenReturn(false);
        when(tagRepository.save(existingTag)).thenReturn(savedTag);
        when(tagMapper.toResponse(savedTag)).thenReturn(expectedResponse);

        TagResponse result = tagService.updateTag(id, request);

        assertEquals(newLabel, result.label());

        verify(tagRepository).save(eq(existingTag));
    }

    @Test
    void updateTag_WhenLabelIsSameAsCurrent_ShouldNotUpdateValues() {
        Long id = 1L;
        String currentLabel = "Spicy";
        TagUpdateRequest request = new TagUpdateRequest(currentLabel);

        Tag existingTag = new Tag();
        existingTag.setId(id);
        existingTag.setLabel(currentLabel);

        TagResponse expectedResponse = new TagResponse(id, currentLabel);

        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(existingTag)).thenReturn(existingTag);
        when(tagMapper.toResponse(existingTag)).thenReturn(expectedResponse);

        tagService.updateTag(id, request);

        verify(tagRepository, never()).existsByLabel(any());
        verify(tagRepository).save(existingTag);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void updateTag_WhenLabelIsNullOrBlank_ShouldNotUpdateValues(String value) {
        Long id = 1L;
        TagUpdateRequest request = new TagUpdateRequest("");

        Tag existingTag = new Tag();
        existingTag.setId(id);
        existingTag.setLabel("Original");

        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.save(existingTag)).thenReturn(existingTag);
        when(tagMapper.toResponse(existingTag)).thenReturn(null);

        tagService.updateTag(id, request);

        verify(tagRepository, never()).existsByLabel(any());
        verify(tagRepository).save(argThat(t -> t.getLabel().equals("Original")));
    }

    @Test
    void updateTag_WhenTagNotFound_ShouldThrowException() {
        Long id = 1L;
        TagUpdateRequest request = new TagUpdateRequest("Any");

        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagService.updateTag(id, request));
        verify(tagRepository, never()).save(any());
    }

    @Test
    void updateTag_WhenNewLabelDuplicate_ShouldThrowException() {
        Long id = 1L;
        String newLabel = "Existing Label";
        TagUpdateRequest request = new TagUpdateRequest(newLabel);

        Tag existingTag = new Tag();
        existingTag.setId(id);
        existingTag.setLabel("Old Label");

        when(tagRepository.findById(id)).thenReturn(Optional.of(existingTag));
        when(tagRepository.existsByLabel(newLabel)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> tagService.updateTag(id, request));

        verify(tagRepository, never()).save(any());
    }
}