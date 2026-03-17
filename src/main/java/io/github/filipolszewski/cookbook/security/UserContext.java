package io.github.filipolszewski.cookbook.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {

    private Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated user");
        }

        return jwt;
    }

    public Long getCurrentUserId() {
        return getJwt().getClaim("userId");
    }

    public String getCurrentUserEmail() {
        return getJwt().getSubject();
    }

}
