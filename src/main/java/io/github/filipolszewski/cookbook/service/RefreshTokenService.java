package io.github.filipolszewski.cookbook.security.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.exception.AccessDeniedException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.model.entity.RefreshToken;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.RefreshTokenRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.UserContext;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        log.debug("Generating new refresh token for user email: {}", userId);

        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken token = new RefreshToken();
        token.setUser(userRepository.getReferenceById(userId));
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(jwtProperties.refreshExpirationSeconds()));

        RefreshToken saved = refreshTokenRepository.save(token);
        log.debug("Successfully generated and saved refresh token for user ID: {}", userId);

        return saved;
    }

    public RefreshToken findByToken(String token) {
        log.debug("Attempting to find refresh token in database");
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AccessDeniedException("Refresh token is not in database!"));
    }

    public void verifyExpiration(RefreshToken token) {
        log.debug("Verifying expiration for refresh token belonging to user ID: {}", token.getUser().getId());

        if (token.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token for user ID: {} has expired. Deleting token.", token.getUser().getId());
            refreshTokenRepository.delete(token);
            throw new AccessDeniedException("Refresh token was expired. Please make a new signin request");
        }

        log.debug("Refresh token is valid");
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        log.info("Deleting all refresh tokens for user ID: {}", userId);
        userRepository.findById(userId).ifPresent(u -> {
            refreshTokenRepository.deleteByUserId(userId);
            log.debug("Successfully deleted refresh tokens for user ID: {}", userId);
        });
    }

}