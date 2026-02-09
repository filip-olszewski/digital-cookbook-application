package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.RecipeIngredientMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.RecipeIngredient;
import io.github.filipolszewski.cookbook.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeIngredientMapper recipeIngredientMapper;

    @InjectMocks
    private RecipeIngredientService recipeIngredientService;

    @ParameterizedTest
    @NullAndEmptySource
    void assembleIngredients_WhenRequestListEmptyOrNull_ShouldReturnEmptyList(List<RecipeIngredientAddRequest> list) {
        List<RecipeIngredient> res = recipeIngredientService.assembleIngredients(list);

        assertThat(res).isNullOrEmpty();
        verifyNoInteractions(ingredientRepository);
        verifyNoInteractions(recipeIngredientMapper);
    }

    @Test
    void assembleIngredients_WhenInvalidIngredientId_ShouldThrowResourceNotFoundException() {
        List<RecipeIngredientAddRequest> requests = List.of(
            new RecipeIngredientAddRequest(1L, 2.0, "g"),
            new RecipeIngredientAddRequest(1001L, 2.0, "g")
        );

        Set<Long> ids = Set.of(1L, 1001L);

        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);

        List<Ingredient> found = List.of(ingredient);

        when(ingredientRepository.findAllById(ids)).thenReturn(found);

        assertThatThrownBy(() -> recipeIngredientService.assembleIngredients(requests))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("One or more ingredients not found");

        verify(recipeIngredientMapper, never()).toEntity(any());
    }

    @Test
    void assembleIngredients_WhenAllValid_ShouldMapCorrectly() {
        Long id = 1L;
        RecipeIngredientAddRequest req = new RecipeIngredientAddRequest(id, 100.0, "g");
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        RecipeIngredient mappedEntity = new RecipeIngredient();

        when(ingredientRepository.findAllById(Set.of(id))).thenReturn(List.of(ingredient));
        when(recipeIngredientMapper.toEntity(req)).thenReturn(mappedEntity);

        List<RecipeIngredient> result = recipeIngredientService.assembleIngredients(List.of(req));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isSameAs(mappedEntity);
        assertThat(result.getFirst().getIngredient()).isEqualTo(ingredient);
    }
}