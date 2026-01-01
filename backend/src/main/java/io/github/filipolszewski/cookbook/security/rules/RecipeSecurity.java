package io.github.filipolszewski.cookbook.security.rules;

import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("recipeSecurity")
@RequiredArgsConstructor
public class RecipeSecurity {

    private final RecipeRepository recipeRepository;

    public boolean isAuthorOrAdmin(Long recipeId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a ->
            a.getAuthority().equals("ADMIN")
        );

        if(isAdmin) return true;

        /* Performance update for the future:
        Eliminate additional join on the recipe call by using userId within the claims */
        return recipeRepository.existsByIdAndAuthorEmail(recipeId, authentication.getName());
    }

}
