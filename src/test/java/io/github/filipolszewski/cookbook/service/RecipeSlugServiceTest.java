package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeSlugServiceTest {

    @Mock
    private Slugify slugify;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeSlugService recipeSlugService;

    @Test
    void generate_WhenSlugDoesNotExist_ShouldReturnBaseSlug() {
        String name = "Test Name";
        String slug = "test-name";

        when(slugify.slugify(name)).thenReturn(slug);
        when(recipeRepository.existsBySlug(slug)).thenReturn(false);

        String res = recipeSlugService.generate(name);

        assertThat(res).isEqualTo(slug);
        verify(recipeRepository, never()).countBySlugStartingWith(any(String.class));
        verify(recipeRepository, atMostOnce()).existsBySlug(slug);
    }

    @Test
    void generate_WhenSlugDuplicate_ShouldReturnSlugWithCounter() {
        String name = "Test Name";
        String slug = "test-name";

        when(slugify.slugify(name)).thenReturn(slug);
        when(recipeRepository.existsBySlug(slug)).thenReturn(true);
        when(recipeRepository.countBySlugStartingWith(slug + "-")).thenReturn(1L);
        when(recipeRepository.existsBySlug("test-name-2")).thenReturn(false);

        String res = recipeSlugService.generate(name);

        assertThat(res).isEqualTo("test-name-2");
        verify(recipeRepository).countBySlugStartingWith(any(String.class));
        verify(recipeRepository).existsBySlug(slug);
    }
}