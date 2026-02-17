package io.github.filipolszewski.cookbook.service;

import com.github.slugify.Slugify;
import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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
        log.debug("Generating slug for recipe name: [{}]", name);
        String baseSlug = slugify.slugify(name);

        if (!recipeRepository.existsBySlug(baseSlug)) {
            log.debug("Generated unique base slug: [{}]", baseSlug);
            return baseSlug;
        }

        log.debug("Slug collision detected for base slug: [{}], resolving collision...", baseSlug);
        long collisionCount = recipeRepository.countBySlugStartingWith(baseSlug + "-");
        String candidate = baseSlug + "-" + (collisionCount + 1);

        while (recipeRepository.existsBySlug(candidate)) {
            collisionCount++;
            candidate = baseSlug + "-" + (collisionCount + 1);
        }

        log.debug("Resolved collision, generated unique slug: [{}]", candidate);
        return candidate;
    }
}