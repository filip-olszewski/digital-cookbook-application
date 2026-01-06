package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.dto.tag.TagCreateRequest;
import io.github.filipolszewski.cookbook.dto.tag.TagResponse;
import io.github.filipolszewski.cookbook.dto.tag.TagUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.TagMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
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

    private final Slugify slugify = Slugify.builder().build();

    public List<TagResponse> getTags() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .toList();
    }

    @Transactional
    public TagResponse addTag(TagCreateRequest request) {

        String slug = slugify.slugify(request.label());
        verifyTagSlugUniqueness(slug);

        Tag tag = tagMapper.toEntity(request);
        tag.setSlug(slug);

        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    @Transactional
    public void deleteTag(Long id) {
        Tag tag = findTagById(id);
        tagRepository.detachTagFromAllRecipes(id);
        tagRepository.delete(tag);
    }

    @Transactional
    public TagResponse updateTag(Long id, TagUpdateRequest request) {

        Tag tag = findTagById(id);

        if(request.label() != null &&
          !request.label().isBlank() &&
          !request.label().equals(tag.getLabel())) {

            tag.setLabel(request.label());

            String slug = slugify.slugify(request.label());
            if(!slug.equals(tag.getSlug())) {
                verifyTagSlugUniqueness(slug);
                tag.setSlug(slug);
            }
        }

        Tag saved = tagRepository.save(tag);
        return tagMapper.toResponse(saved);
    }

    private Tag findTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Tag.class, "id", id)));
    }

    private void verifyTagSlugUniqueness(String slug) {
        if(tagRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Tag.class, "slug", slug));
        }
    }
}
