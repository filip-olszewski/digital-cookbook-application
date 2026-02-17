package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.recipeingredient.RecipeIngredientAddRequest;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.RecipeIngredientMapper;
import io.github.filipolszewski.cookbook.model.entity.BaseEntity;
import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.RecipeIngredient;
import io.github.filipolszewski.cookbook.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeIngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientMapper recipeIngredientMapper;

    public List<RecipeIngredient> assembleIngredients(List<RecipeIngredientAddRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            log.debug("No recipe ingredients to assemble.");
            return List.of();
        }

        log.debug("Assembling [{}] recipe ingredients", requests.size());

        Set<Long> ids = requests.stream()
                .map(RecipeIngredientAddRequest::ingredientId)
                .collect(Collectors.toSet());

        Map<Long, Ingredient> ingredients = ingredientRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(BaseEntity::getId, i -> i));

        if (ingredients.size() != ids.size()) {
            throw new ResourceNotFoundException("One or more ingredients not found");
        }

        List<RecipeIngredient> assembled = requests.stream()
                .map(req -> {
                    RecipeIngredient ri = recipeIngredientMapper.toEntity(req);
                    ri.setIngredient(ingredients.get(req.ingredientId()));
                    return ri;
                })
                .toList();

        log.debug("Successfully assembled [{}] recipe ingredients", assembled.size());
        return assembled;
    }
}