package io.github.filipolszewski.cookbook.security.rules;

import io.github.filipolszewski.cookbook.repository.RecipeRepository;
import io.github.filipolszewski.cookbook.repository.ReviewRepository;
import io.github.filipolszewski.cookbook.security.CustomUserPrincipal;
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
        return isAuthor(reviewId, authentication);
    }

    public boolean isAuthor(Long reviewId, Authentication authentication) {
        if(authentication.getPrincipal() instanceof CustomUserPrincipal p) {
            return reviewRepository.existsByIdAndUserId(reviewId, p.getId());
        }
        return false;
    }

}
