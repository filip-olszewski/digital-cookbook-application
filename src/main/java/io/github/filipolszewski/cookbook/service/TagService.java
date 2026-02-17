package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.TagMapper;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import io.github.filipolszewski.cookbook.repository.TagRepository;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import io.github.filipolszewski.cookbook.util.UpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagResponse> getTags() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Transactional
    public TagResponse addTag(TagCreateRequest request) {
        String newLabel = request.label();
        log.info("Creating new tag with label: {}", newLabel);

        checkExistsByLabel(newLabel);

        Tag tag = tagMapper.toEntity(request);
        Tag saved = tagRepository.save(tag);

        log.info("Successfully created tag with ID: {}", saved.getId());
        return tagMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTag(Long id) {
        log.info("Deleting tag with ID: {}", id);
        tagRepository.detachTagFromAllRecipes(id);
        tagRepository.delete(findTagById(id));
        log.info("Successfully deleted tag with ID: {}", id);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagUpdateRequest request) {
        log.info("Updating tag with ID: {}", id);
        Tag tag = findTagById(id);
        String newLabel = request.label();

        if(UpdateUtil.isChanged(newLabel, tag.getLabel())) {
            checkExistsByLabel(newLabel);
            tag.setLabel(newLabel);
            log.info("Tag ID: {} label updated to: {}", id, newLabel);
        }

        return tagMapper.toResponse(tagRepository.save(tag));
    }

    public Set<Tag> findTagsByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return Set.of();

        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException("One or more tags not found");
        }

        return new HashSet<>(tags);
    }

    private Tag findTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Tag.class, "id", id)));
    }

    private void checkExistsByLabel(String label) {
        if(tagRepository.existsByLabel(label)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Tag.class, "label", label));
        }
    }
}