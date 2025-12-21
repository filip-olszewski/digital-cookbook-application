package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.mapper.IngredientMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.enumeration.IngredientType;
import io.github.filipolszewski.cookbook.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public Page<IngredientSummaryResponse> getIngredients(Pageable pageable, String name) {
        if(name != null && !name.isBlank()) {
            return ingredientRepository.findByNameContainingIgnoreCase(name, pageable)
                    .map(ingredientMapper::toSummary);
        }
        else return ingredientRepository.findAll(pageable)
                .map(ingredientMapper::toSummary);
    }

    public IngredientSummaryResponse addIngredient(IngredientCreateRequest request) {
        if(ingredientRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Ingredient with this name already exists!");
        }

        return ingredientMapper.toSummary(ingredientRepository.save(
                ingredientMapper.toEntity(request)
        ));
    }

}
