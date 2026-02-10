package io.github.filipolszewski.cookbook.security.rules;

import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.security.CustomUserPrincipal;
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
        return isAuthor(recipeId, authentication);
    }

    public boolean isAuthor(Long recipeId, Authentication authentication) {
        if(authentication.getPrincipal() instanceof CustomUserPrincipal p) {
            return recipeRepository.existsByIdAndAuthorId(recipeId, p.getId());
        }
        return false;
    }

}
