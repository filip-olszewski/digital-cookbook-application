package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceConflictException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.IngredientMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import io.github.filipolszewski.cookbook.repository.IngredientRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void getIngredients_WhenNameIsInvalid_ShouldReturnAll(String nameQuery) {
        Long id = 1L;
        String name = "carrot";
        IngredientType type = IngredientType.VEGETABLE;

        Pageable pageable = PageRequest.of(0, 10);

        var ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setType(type);

        var dto = new IngredientSummaryResponse(id, name, type);

        Page<Ingredient> page = new PageImpl<>(List.of(ingredient));

        when(ingredientRepository.findAll(pageable)).thenReturn(page);
        when(ingredientMapper.toSummary(ingredient)).thenReturn(dto);

        Page<IngredientSummaryResponse> expected = new PageImpl<>(List.of(dto));

        Page<IngredientSummaryResponse> res = ingredientService
                .getIngredients(pageable, nameQuery);

        assertNotNull(res);
        assertEquals(expected.getContent(), res.getContent());
        assertEquals(expected.getTotalElements(), res.getTotalElements());

        verify(ingredientRepository).findAll(eq(pageable));
        verify(ingredientRepository, never()).findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void getIngredients_WhenNameProvided_ShouldReturnFiltered() {
        Long id = 1L;
        String name = "carrot";
        IngredientType type = IngredientType.VEGETABLE;
        String nameQuery = "car";

        Pageable pageable = PageRequest.of(0, 10);

        var ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setType(type);

        var dto = new IngredientSummaryResponse(id, name, type);

        Page<Ingredient> page = new PageImpl<>(List.of(ingredient));

        when(ingredientRepository.findByNameContainingIgnoreCase(nameQuery, pageable))
                .thenReturn(page);
        when(ingredientMapper.toSummary(ingredient)).thenReturn(dto);

        Page<IngredientSummaryResponse> expected = new PageImpl<>(List.of(dto));

        Page<IngredientSummaryResponse> res = ingredientService
                .getIngredients(pageable, nameQuery);

        assertNotNull(res);
        assertEquals(expected.getContent(), res.getContent());
        assertEquals(expected.getTotalElements(), res.getTotalElements());

        verify(ingredientRepository).findByNameContainingIgnoreCase(eq(nameQuery), eq(pageable));
        verify(ingredientRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void addIngredient_WhenNameIsUnique_ShouldSaveAndReturnIngredient() {
        Long id = 1L;
        String name = "carrot";
        IngredientType type = IngredientType.VEGETABLE;
        var request = new IngredientCreateRequest(name, type);

        var ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setType(IngredientType.VEGETABLE);

        var saved = new Ingredient();
        saved.setId(id);
        saved.setName("carrot");
        saved.setType(IngredientType.VEGETABLE);

        var expected = new IngredientSummaryResponse(id, name, type);

        when(ingredientRepository.existsByName(name)).thenReturn(false);
        when(ingredientMapper.toEntity(request)).thenReturn(ingredient);
        when(ingredientRepository.save(ingredient)).thenReturn(saved);
        when(ingredientMapper.toSummary(saved)).thenReturn(expected);

        IngredientSummaryResponse res = ingredientService.addIngredient(request);

        assertNotNull(res);
        assertEquals(expected, res);

        verify(ingredientRepository).save(eq(ingredient));
    }

    @Test
    void addIngredient_WhenNameDuplicate_ShouldThrowResourceAlreadyExistsException() {
        String name = "carrot";
        var request = new IngredientCreateRequest(name, IngredientType.VEGETABLE);

        when(ingredientRepository.existsByName(name)).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            ingredientService.addIngredient(request);
        });

        assertEquals("Ingredient with this name ['carrot'] already exists.", ex.getMessage());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void deleteIngredient_WhenIngredientDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long id = 1L;

        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            ingredientService.deleteIngredient(id);
        });

        assertEquals("Ingredient with id ['1'] not found.", ex.getMessage());

        verify(ingredientRepository).findById(eq(id));
        verify(ingredientRepository, never()).delete(any());
    }

    @Test
    void deleteIngredient_WhenIngredientUsedByRecipes_ShouldThrowResourceConflictException() {
        Long id = 1L;
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(recipeRepository.existsByIngredientId(id)).thenReturn(true); // Or isIngredientUsed(id) depending on your rename

        ResourceConflictException ex = assertThrows(ResourceConflictException.class, () -> {
            ingredientService.deleteIngredient(id);
        });

        assertEquals("Cannot delete ingredient which is being used in active recipes",
                ex.getMessage());

        verify(ingredientRepository).findById(eq(id));
        verify(recipeRepository).existsByIngredientId(eq(id));
        verify(ingredientRepository, never()).delete(any());
    }

    @Test
    void deleteIngredient_WhenIngredientNotUsed_ShouldDelete() {
        Long id = 1L;
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(recipeRepository.existsByIngredientId(id)).thenReturn(false);

        ingredientService.deleteIngredient(id);

        verify(ingredientRepository).findById(eq(id));
        verify(recipeRepository).existsByIngredientId(eq(id));
        verify(ingredientRepository).delete(ingredient);
    }

    @Test
    void updateIngredient_WhenIngredientDoesNotExist_ShouldThrowResourceNotFoundException() {
        Long id = 1L;
        var request = new IngredientUpdateRequest(null, null);

        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            ingredientService.updateIngredient(id, request);
        });

        assertEquals("Ingredient with id ['1'] not found.", ex.getMessage());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void updateIngredient_WhenNameDuplicate_ShouldThrowResourceAlreadyExistsException() {
        Long id = 1L;
        String newName = "new name";
        var request = new IngredientUpdateRequest(newName, null);

        var ingredient = new Ingredient();
        ingredient.setId(id);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.existsByName(newName)).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            ingredientService.updateIngredient(id, request);
        });

        assertEquals("Ingredient with this name ['new name'] already exists.", ex.getMessage());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void updateIngredient_WhenOnlyNameProvided_ShouldUpdateNameAndReturnIngredient() {
        Long id = 1L;
        String newName = "new name";

        var request = new IngredientUpdateRequest(newName, null);

        var ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName("old name");

        var saved = new Ingredient();
        saved.setId(id);
        saved.setName(newName);

        var expected = new IngredientSummaryResponse(id, newName, IngredientType.DAIRY);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.existsByName(newName)).thenReturn(false);
        when(ingredientRepository.save(ingredient)).thenReturn(saved);
        when(ingredientMapper.toSummary(saved)).thenReturn(expected);

        IngredientSummaryResponse res = ingredientService.updateIngredient(id, request);

        assertEquals(expected, res);
        verify(ingredientRepository).save(eq(ingredient));
    }

    @Test
    void updateIngredient_WhenOnlyTypeProvided_ShouldUpdateTypeAndReturnIngredient() {
        Long id = 1L;
        IngredientType type = IngredientType.DAIRY;
        var request = new IngredientUpdateRequest(null, type);

        var ingredient = new Ingredient();
        ingredient.setId(id);

        var saved = new Ingredient();
        saved.setId(id);
        saved.setType(type);

        var expected = new IngredientSummaryResponse(id, null, type);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(ingredient)).thenReturn(saved);
        when(ingredientMapper.toSummary(saved)).thenReturn(expected);

        IngredientSummaryResponse res = ingredientService.updateIngredient(id, request);

        assertEquals(expected, res);
        verify(ingredientRepository, never()).existsByName(any());
        verify(ingredientRepository).save(eq(ingredient));
    }

    @Test
    void updateIngredient_WhenBothFieldsProvided_ShouldUpdateBoth() {
        Long id = 1L;
        String newName = "New Name";
        IngredientType newType = IngredientType.FRUIT;
        var request = new IngredientUpdateRequest(newName, newType);

        var ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName("Old Name");
        ingredient.setType(IngredientType.VEGETABLE);

        var saved = new Ingredient();
        saved.setId(id);
        saved.setName(newName);
        saved.setType(newType);

        var expected = new IngredientSummaryResponse(id, newName, newType);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.existsByName(newName)).thenReturn(false);
        when(ingredientRepository.save(ingredient)).thenReturn(saved);
        when(ingredientMapper.toSummary(saved)).thenReturn(expected);

        IngredientSummaryResponse res = ingredientService.updateIngredient(id, request);

        assertEquals(expected, res);
    }
}