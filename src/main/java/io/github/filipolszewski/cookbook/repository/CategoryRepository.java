package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {
    boolean existsBySlug(String slug);
    Optional<Category> findBySlug(String slug);
    boolean existsByParentCategoryId(Long parentCategoryId);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subCategories WHERE c.id = :id")
    Optional<Category> findByIdWithSubcategories(Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory WHERE c.id = :id")
    Optional<Category> findByIdWithParent(Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parentCategory WHERE c.slug = :slug")
    Optional<Category> findBySlugWithParent(String slug);
}
