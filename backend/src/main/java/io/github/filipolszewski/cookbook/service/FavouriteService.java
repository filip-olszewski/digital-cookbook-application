package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.dto.recipe.RecipeSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceConflictException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.RecipeMapper;
import io.github.filipolszewski.cookbook.model.entity.Favourite;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.FavouriteRepository;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.UserContext;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;

    private final RecipeMapper recipeMapper;

    @Transactional
    public void addToFavourites(Long recipeId) {
        long userId = userContext.getCurrentUserId();

        if(favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new ResourceConflictException("User already liked this recipe!");
        }

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new ResourceNotFoundException(
            ErrorMessageUtil.notFound(Recipe.class, "id", recipeId)
        ));

        User user = userRepository.getReferenceById(userId); // Only proxy

        Favourite favourite = new Favourite();
        favourite.setRecipe(recipe);
        favourite.setUser(user);
        favourite.setLikeDate(Instant.now());

        favouriteRepository.save(favourite);

        recipe.addFavouriteCount();
    }

    @Transactional
    public void removeFromFavourites(Long recipeId) {
        long userId = userContext.getCurrentUserId();

        if(!favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)) {
            throw new ResourceConflictException("User has not liked this recipe!");
        }

        favouriteRepository.deleteByUserIdAndRecipeId(userId, recipeId);

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new ResourceNotFoundException(
                ErrorMessageUtil.notFound(Recipe.class, "id", recipeId)
        ));
        recipe.removeFavouriteCount();
    }

    public Page<RecipeSummaryResponse> getUserFavourites(Pageable pageable) {
        long userId = userContext.getCurrentUserId();
        return recipeRepository.findFavouriteRecipesByUserId(userId, pageable)
                .map(recipeMapper::toSummary);
    }
}
