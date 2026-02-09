package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Favourite;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    long countByUserId(Long userId);
    boolean existsByUserIdAndRecipeId(Long userId, Long recipeId);
    Page<Favourite> findAllByUserId(Long userId);

    @Modifying
    @Query("DELETE f FROM Favourite f WHERE f.user.id = :userId AND f.recipe.id = :recipeId")
    void deleteByUserIdAndRecipeId(Long userId, Long recipeId);
}
