package io.github.filipolszewski.cookbook.security;

import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {

    private CustomUserPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserPrincipal p)) {
            throw new IllegalStateException("No authenticated user");
        }

        return p;
    }

    public Long getCurrentUserId() {
        return getPrincipal().getId();
    }

    public String getCurrentUserEmail() {
        return getPrincipal().getEmail();
    }

}
