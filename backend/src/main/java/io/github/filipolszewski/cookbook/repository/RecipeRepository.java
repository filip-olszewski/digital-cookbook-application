package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
        JpaSpecificationExecutor<Recipe> {

    boolean existsBySlug(String slug);
    boolean existsByCategoryId(Long categoryId);
    boolean existsByIdAndAuthorId(Long id, Long authorId);
    long countBySlugStartingWith(String prefix);
    long countByAuthorId(Long authorId);

    @Query("SELECT COUNT(*) FROM Recipe r WHERE r.author.username = :username")
    long countByAuthorUsername(String username);

    @EntityGraph(attributePaths = {"tags", "author", "category"})
    Page<Recipe> findAll(Specification<Recipe> specification, Pageable pageable);

    @EntityGraph(attributePaths = {
            "recipeIngredients", "steps", "tags",
            "author", "category", "recipeIngredients.ingredient"
    })
    Optional<Recipe> findBySlug(String slug);

    @Query("""
        SELECT COUNT(*) > 0 FROM Recipe r 
        JOIN r.recipeIngredients ri WHERE ri.ingredient.id = :ingredientId
    """)
    boolean existsByIngredientId(Long ingredientId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Recipe r 
            SET r.averageRating = ((r.averageRating * r.reviewCount) + :rating) / (r.reviewCount + 1),
                r.reviewCount = r.reviewCount + 1 
                WHERE r.id = :id
            """)
    void addReviewRating(Long id, int rating);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Recipe r
            SET r.averageRating = CASE 
                WHEN r.reviewCount <= 1 
                THEN 0.0 
                ELSE ((r.averageRating * r.reviewCount) - :rating) / (r.reviewCount - 1)
            END,
                r.reviewCount = r.reviewCount -1
            WHERE r.id = :id AND r.reviewCount > 0
            """)
    void removeReviewRating(Long id, int rating);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Recipe r 
            SET r.averageRating = ((r.averageRating * r.reviewCount) + :newRating - :oldRating) / r.reviewCount 
            WHERE r.id = :id
            """)
    void updateReviewRating(Long id, int oldRating, int newRating);

    @Query("""
        SELECT r 
        FROM Favourite f 
        JOIN f.recipe r 
        WHERE f.user.id = :userId 
        ORDER BY f.likeDate DESC
    """)
    @EntityGraph(attributePaths = {"tags", "author", "category"})
    Page<Recipe> findFavouriteRecipesByUserId(Long userId, Pageable pageable);
}
