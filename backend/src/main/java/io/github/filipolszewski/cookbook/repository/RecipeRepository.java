package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
        JpaSpecificationExecutor<Recipe> {
    Optional<Recipe> findBySlug(String slug);

    @Query("SELECT COALESCE(AVG(rv.rating), 0.0) FROM Review rv WHERE rv.recipe.id = :id")
    Double getRecipeAverageRating(Long id);

    @Query("SELECT COUNT(*) FROM Review rv WHERE rv.recipe.id = :id")
    Integer getRecipeReviewCount(Long id);

    @Query("SELECT COUNT(*) FROM Recipe r WHERE r.author.username = :username")
    Integer getRecipeCountByAuthor(String username);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
        SELECT COUNT(*) > 0 FROM Recipe r 
        JOIN r.recipeIngredients ri WHERE ri.ingredient.id = :ingredientId
    """)
    boolean isIngredientUsed(Long ingredientId);

    boolean existsByIdAndAuthorEmail(Long id, String email);
}
