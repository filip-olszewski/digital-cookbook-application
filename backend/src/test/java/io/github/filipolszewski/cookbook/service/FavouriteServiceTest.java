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
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private RecipeMapper recipeMapper;

    @InjectMocks
    private FavouriteService favouriteService;

    @Test
    void addToFavourites_WhenValid_ShouldSaveAndIncrementCount() {
        Long userId = 100L;
        Long recipeId = 50L;

        User userProxy = new User();
        userProxy.setId(userId);

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setFavouriteCount(10);

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepository.getReferenceById(userId)).thenReturn(userProxy);

        favouriteService.addToFavourites(recipeId);

        ArgumentCaptor<Favourite> captor = ArgumentCaptor.forClass(Favourite.class);
        verify(favouriteRepository).save(captor.capture());

        Favourite savedFav = captor.getValue();

        assertThat(savedFav.getRecipe()).isEqualTo(recipe);
        assertThat(savedFav.getUser()).isEqualTo(userProxy);
        assertThat(savedFav.getLikeDate()).isNotNull();
        assertThat(recipe.getFavouriteCount()).isEqualTo(11);
    }

    @Test
    void addToFavourites_WhenAlreadyLiked_ShouldThrowConflict() {
        Long userId = 1L;
        Long recipeId = 2L;

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(true);

        assertThatThrownBy(() -> favouriteService.addToFavourites(recipeId))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("User already liked this recipe!");


        verifyNoInteractions(recipeRepository);
        verify(favouriteRepository, never()).save(any());
    }

    @Test
    void addToFavourites_WhenRecipeNotFound_ShouldThrowNotFound() {
        Long userId = 1L;
        Long recipeId = 999L;

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favouriteService.addToFavourites(recipeId))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(favouriteRepository, never()).save(any());
    }

    @Test
    void removeFromFavourites_WhenValid_ShouldDeleteAndDecrementCount() {
        Long userId = 100L;
        Long recipeId = 50L;

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setFavouriteCount(10);

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(true);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        favouriteService.removeFromFavourites(recipeId);

        verify(favouriteRepository).deleteByUserIdAndRecipeId(userId, recipeId);
        assertThat(recipe.getFavouriteCount()).isEqualTo(9);
    }

    @Test
    void removeFromFavourites_WhenNotLiked_ShouldThrowConflict() {
        Long userId = 1L;
        Long recipeId = 2L;

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(favouriteRepository.existsByUserIdAndRecipeId(userId, recipeId)).thenReturn(false);

        assertThatThrownBy(() -> favouriteService.removeFromFavourites(recipeId))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("User has not liked this recipe!");

        verify(favouriteRepository, never()).deleteByUserIdAndRecipeId(anyLong(), anyLong());
    }

    @Test
    void getUserFavourites_ShouldReturnMappedPage() {
        Long userId = 100L;
        Pageable pageable = Pageable.ofSize(10);

        Recipe recipe = new Recipe();
        RecipeSummaryResponse summary = Instancio.create(RecipeSummaryResponse.class);

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

        when(userContext.getCurrentUserId()).thenReturn(userId);
        when(recipeRepository.findFavouriteRecipesByUserId(userId, pageable))
                .thenReturn(recipePage);
        when(recipeMapper.toSummary(recipe)).thenReturn(summary);

        Page<RecipeSummaryResponse> result = favouriteService.getUserFavourites(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(summary);
        verify(recipeRepository).findFavouriteRecipesByUserId(userId, pageable);
    }
}