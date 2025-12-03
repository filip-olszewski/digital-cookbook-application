package io.github.filipolszewski.cookbook.repository;

import io.github.filipolszewski.cookbook.model.entity.Ingredient;
import io.github.filipolszewski.cookbook.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
        JpaSpecificationExecutor<Recipe> {

}
