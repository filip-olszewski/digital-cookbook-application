package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByLabel(String label);

    @Modifying
    @Query(value = "DELETE FROM recipe_tags WHERE tag_id = :tagId", nativeQuery = true)
    void detachTagFromAllRecipes(Long tagId);
}
