package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.TagMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.Tag;
import io.github.filipolszewski.cookbook.repository.TagRepository;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        String slug = Slugify.builder().build().slugify(request.label());

        if(tagRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Tag.class, "slug", slug));
        }

        Tag tag = tagMapper.toEntity(request);
        tag.setSlug(slug);

        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTag(Long id) {
        if(!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ErrorMessageUtil.notFound(Tag.class, "id", id));
        }

        tagRepository.deleteById(id);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagUpdateRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Tag.class, "id", id)));

        if(request.label() != null &&
          !request.label().isBlank() &&
          !request.label().equals(tag.getLabel())) {
            String slug = Slugify.builder().build().slugify(request.label());

            if(tagRepository.existsBySlug(slug)) {
                throw new ResourceAlreadyExistsException(
                        ErrorMessageUtil.exists(Tag.class, "slug", slug));
            }

            tag.setLabel(request.label());
            tag.setSlug(slug);
        }

        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }
}
