package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeSlugService {

    private final Slugify slugify;
    private final RecipeRepository recipeRepository;

    /**
    * Generates a unique slug for a recipe. If a slug created based on the recipe name is unavailable,
    * we count the N slugs with the same prefixes there are. As some of them could have gotten deleted,
    * we iterate while checking if a slug exists (should not take more than 1 iteration, since we start
    * at the Nth slug)
    * @param name      The name based on which we generate the slug.
    * @return          A unique slug for the recipe.
    */
    public String generate(String name) {
        String baseSlug = slugify.slugify(name);

        if (!recipeRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        long collisionCount = recipeRepository.countBySlugStartingWith(baseSlug + "-");
        String candidate = baseSlug + "-" + (collisionCount + 1);

        while (recipeRepository.existsBySlug(candidate)) {
            collisionCount++;
            candidate = baseSlug + "-" + (collisionCount + 1);
        }

        return candidate;
    }
}
