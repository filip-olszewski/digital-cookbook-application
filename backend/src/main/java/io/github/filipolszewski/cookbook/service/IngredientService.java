package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.ingredient.IngredientCreateRequest;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientSummaryResponse;
import io.github.filipolszewski.cookbook.dto.ingredient.IngredientUpdateRequest;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceConflictException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.IngredientMapper;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.repository.IngredientRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    private final RecipeRepository recipeRepository;

    public Page<IngredientSummaryResponse> getIngredients(Pageable pageable, String name) {
        if(name != null && !name.isBlank()) {
            return ingredientRepository.findByNameContainingIgnoreCase(name, pageable)
                    .map(ingredientMapper::toSummary);
        }
        else return ingredientRepository.findAll(pageable)
                .map(ingredientMapper::toSummary);
    }

    @Transactional
    public IngredientSummaryResponse addIngredient(IngredientCreateRequest request) {
        verifyIngredientNameUniqueness(request.name());
        Ingredient saved = ingredientRepository.save(ingredientMapper.toEntity(request));
        return ingredientMapper.toSummary(saved);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        if(recipeRepository.existsByIngredientId(id)) {
            throw new ResourceConflictException(
                    "Cannot delete ingredient which is being used in active recipes");
        }

        ingredientRepository.delete(findIngredientById(id));
    }

    @Transactional
    public IngredientSummaryResponse updateIngredient(Long id, IngredientUpdateRequest request) {
        Ingredient ingredient = findIngredientById(id);

        if(request.name() != null &&
          !request.name().isBlank() &&
          !request.name().equals(ingredient.getName())
        ) {
            verifyIngredientNameUniqueness(request.name());
            ingredient.setName(request.name());
        }

        if(request.type() != null) {
            ingredient.setType(request.type());
        }

        Ingredient saved = ingredientRepository.save(ingredient);
        return ingredientMapper.toSummary(saved);
    }

    private Ingredient findIngredientById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessageUtil.notFound(Ingredient.class, "id", id)));
    }

    private void verifyIngredientNameUniqueness(String name) {
        if(ingredientRepository.existsByName(name)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(Ingredient.class, "name", name));
        }
    }

}
