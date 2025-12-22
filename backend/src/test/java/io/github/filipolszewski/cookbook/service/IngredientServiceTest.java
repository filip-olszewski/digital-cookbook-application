package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.IngredientMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import io.github.filipolszewski.cookbook.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void addIngredient_WhenNameIsUnique_ShouldSaveAndReturnIngredient() {
        var request = new IngredientCreateRequest("carrot", IngredientType.VEGETABLE);

        var entity = new Ingredient();
        entity.setName("carrot");
        entity.setType(IngredientType.VEGETABLE);

        var savedEntity = new Ingredient();
        savedEntity.setId(1L);
        savedEntity.setName("carrot");
        savedEntity.setType(IngredientType.VEGETABLE);

        var expectedResponse = new IngredientSummaryResponse(1L, "carrot", IngredientType.VEGETABLE);

        when(ingredientRepository.existsByName("carrot")).thenReturn(false);
        when(ingredientMapper.toEntity(request)).thenReturn(entity);
        when(ingredientRepository.save(entity)).thenReturn(savedEntity);
        when(ingredientMapper.toSummary(savedEntity)).thenReturn(expectedResponse);

        IngredientSummaryResponse actualResponse = ingredientService.addIngredient(request);
        assertEquals(expectedResponse, actualResponse);

        verify(ingredientRepository, times(1)).save(entity);
    }

    @Test
    void addIngredient_WhenNameExists_ShouldThrowException() {
        var request = new IngredientCreateRequest("carrot", IngredientType.VEGETABLE);

        when(ingredientRepository.existsByName("carrot")).thenReturn(true);

        var ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
            ingredientService.addIngredient(request);
        });

        assertEquals("Ingredient with this name already exists!", ex.getMessage());
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void getIngredients_WhenNameIsNull_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        var entity = new Ingredient();
        entity.setId(1L);
        entity.setName("carrot");
        entity.setType(IngredientType.VEGETABLE);

        Page<Ingredient> page = new PageImpl<>(List.of(entity));

        when(ingredientRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        var dto = new IngredientSummaryResponse(
            1L,
            "carrot",
            IngredientType.VEGETABLE
        );

        when(ingredientMapper.toSummary(entity)).thenReturn(dto);

        Page<IngredientSummaryResponse> res = ingredientService
                .getIngredients(pageable, null);

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
        assertEquals("carrot", res.getContent().getFirst().name());

        verify(ingredientRepository).findAll(any(Pageable.class));
        verify(ingredientRepository, never())
                .findByNameContainingIgnoreCase(any(), any());
    }

    @Test
    void getIngredients_WhenNameIsBlank_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = " ";

        var entity = new Ingredient();
        entity.setId(1L);
        entity.setName("carrot");
        entity.setType(IngredientType.VEGETABLE);

        Page<Ingredient> page = new PageImpl<>(List.of(entity));

        when(ingredientRepository.findAll(any(Pageable.class))).thenReturn(page);

        var dto = new IngredientSummaryResponse(
            1L,
            "carrot",
            IngredientType.VEGETABLE
        );

        when(ingredientMapper.toSummary(entity)).thenReturn(dto);

        Page<IngredientSummaryResponse> res = ingredientService
                .getIngredients(pageable, name);

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
        assertEquals("carrot", res.getContent().getFirst().name());

        verify(ingredientRepository).findAll(any(Pageable.class));
        verify(ingredientRepository, never()).findByNameContainingIgnoreCase(
                any(), any()
        );
    }

    @Test
    void getIngredients_WhenNameProvided_ShouldReturnFiltered() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "car";

        var entity = new Ingredient();
        entity.setId(1L);
        entity.setName("carrot");
        entity.setType(IngredientType.VEGETABLE);

        Page<Ingredient> page = new PageImpl<>(List.of(entity));

        when(ingredientRepository.findByNameContainingIgnoreCase(
            eq(name), any(Pageable.class)
        )).thenReturn(page);

        var dto = new IngredientSummaryResponse(
            1L,
            "carrot",
            IngredientType.VEGETABLE
        );

        when(ingredientMapper.toSummary(entity)).thenReturn(dto);

        Page<IngredientSummaryResponse> res = ingredientService
                .getIngredients(pageable, name);

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
        assertEquals("carrot", res.getContent().getFirst().name());

        verify(ingredientRepository).findByNameContainingIgnoreCase(
                eq(name), any(Pageable.class)
        );
        verify(ingredientRepository, never()).findAll(any(Pageable.class));
    }
}