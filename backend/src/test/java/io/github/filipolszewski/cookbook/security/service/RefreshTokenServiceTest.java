package io.github.filipolszewski.cookbook.security.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.exception.AccessDeniedException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.model.entity.RefreshToken;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.repository.RefreshTokenRepository;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private UserRepository userRepository;
    @Mock private JwtProperties jwtProperties;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void createRefreshToken_WhenUserExists_ShouldDeleteOldAndSaveNew() {
        String email = "test@example.com";
        User user = new User();
        user.setId(100L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtProperties.refreshExpirationSeconds()).thenReturn(86400L);
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        RefreshToken result = refreshTokenService.createRefreshToken(email);


        verify(refreshTokenRepository).deleteByUserId(100L);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getToken()).isNotNull();
        assertThat(result.getExpiryDate()).isAfter(Instant.now());
    }

    @Test
    void createRefreshToken_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.createRefreshToken("ghost@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByToken_WhenExists_ShouldReturnToken() {
        RefreshToken token = new RefreshToken();
        when(refreshTokenRepository.findByToken("uuid-123")).thenReturn(Optional.of(token));

        RefreshToken result = refreshTokenService.findByToken("uuid-123");
        assertThat(result).isEqualTo(token);
    }

    @Test
    void findByToken_WhenMissing_ShouldThrowAccessDenied() {
        when(refreshTokenRepository.findByToken("uuid-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.findByToken("uuid-123"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Refresh token is not in database!");
    }

    @Test
    void verifyExpiration_WhenTokenNotExpired_ShouldDoNothing() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(3600));

        refreshTokenService.verifyExpiration(token);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_WhenTokenExpired_ShouldDeleteAndThrowException() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(3600));

        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(token))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("expired");

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    void deleteByUserId_WhenUserExists_ShouldDeleteTokens() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        refreshTokenService.deleteByUserId(userId);

        verify(refreshTokenRepository).deleteByUserId(userId);
    }

    @Test
    void deleteByUserId_WhenUserNotFound_ShouldDoNothing() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        refreshTokenService.deleteByUserId(userId);

        verify(refreshTokenRepository, never()).deleteByUserId(anyLong());
    }
}