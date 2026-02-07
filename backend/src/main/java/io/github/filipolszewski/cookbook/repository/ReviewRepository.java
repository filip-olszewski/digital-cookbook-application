package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Recipe;
import io.github.filipolszewski.cookbook.model.entity.Review;
import io.github.filipolszewski.cookbook.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllReviewsByRecipeSlug(String slug, Pageable pageable);
    Page<Review> findAllReviewsByUserUsername(String username, Pageable pageable);
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    boolean existsByIdAndUserEmail(Long id, String email);

    @EntityGraph(attributePaths = {"recipe"})
    @Query("SELECT r FROM Review r WHERE r.id = :id")
    Optional<Review> findByIdWithRecipe(Long id);

    @EntityGraph(attributePaths = {"user", "recipe"})
    @Query("SELECT r FROM Review r WHERE r.id = :id")
    Optional<Review> findByIdWithRecipeAndUser(Long id);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT r FROM Review r WHERE r.recipe.slug = :slug")
    Page<Review> findAllReviewsByRecipeSlugWithUser(String slug, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT r FROM Review r WHERE r.user.username = :username")
    Page<Review> findAllReviewsByUserUsernameWithUser(String username, Pageable pageable);
}
