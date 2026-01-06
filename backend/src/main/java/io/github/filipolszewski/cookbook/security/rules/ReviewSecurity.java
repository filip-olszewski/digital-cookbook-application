package io.github.filipolszewski.cookbook.security.rules;

import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("reviewSecurity")
@RequiredArgsConstructor
public class ReviewSecurity {

    private final ReviewRepository reviewRepository;

    public boolean isAuthorOrAdmin(Long reviewId, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a ->
            a.getAuthority().equals("ADMIN")
        );

        if(isAdmin) return true;

        /* Performance update for the future:
        Eliminate additional join on the recipe call by using userId within the claims */
        return isAuthor(reviewId, authentication);
    }

    public boolean isAuthor(Long reviewId, Authentication authentication) {
        return reviewRepository.existsByIdAndUserEmail(reviewId, authentication.getName());
    }

}
