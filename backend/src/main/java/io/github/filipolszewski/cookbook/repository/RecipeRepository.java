package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
        JpaSpecificationExecutor<Recipe> {
    Optional<Recipe> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query("SELECT COALESCE(AVG(rv.rating), 0.0) FROM Review rv WHERE rv.recipe.id = :id")
    Double getAverageRating(Long id);

    @Query("SELECT COUNT(*) FROM Review rv WHERE rv.recipe.id = :id")
    Integer countReviewsByRecipeId(Long id);

    @Query("SELECT COUNT(*) FROM Recipe r WHERE r.author.username = :username")
    Integer countByAuthorUsername(String username);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
        SELECT COUNT(*) > 0 FROM Recipe r 
        JOIN r.recipeIngredients ri WHERE ri.ingredient.id = :ingredientId
    """)
    boolean existsByIngredientId(Long ingredientId);

    boolean existsByIdAndAuthorEmail(Long id, String email);

    @Query("SELECT r.slug FROM Recipe r WHERE r.slug LIKE :slug%")
    List<String> findSlugsStartingWith(String slug);
}
