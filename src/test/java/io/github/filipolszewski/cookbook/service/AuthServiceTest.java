package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.dto.auth.AuthResponse;
import io.github.filipolszewski.cookbook.dto.auth.LoginRequest;
import io.github.filipolszewski.cookbook.dto.auth.SignupRequest;
import io.github.filipolszewski.cookbook.dto.auth.TokenRefreshRequest;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.UserMapper;
import io.github.filipolszewski.cookbook.model.entity.RefreshToken;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.service.RefreshTokenService;
import io.github.filipolszewski.cookbook.security.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_WhenCredentialsAreValid_ShouldReturnAuthResponse() {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        Authentication auth = mock(Authentication.class);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-uuid");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getName()).thenReturn("test@example.com");
        when(tokenService.generateToken(auth)).thenReturn("jwt-access-token");
        when(refreshTokenService.createRefreshToken("test@example.com")).thenReturn(refreshToken);
        when(jwtProperties.expirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-uuid");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_WhenCredentialsInvalid_ShouldThrowException() {
        LoginRequest request = new LoginRequest("wrong@example.com", "wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void signup_WhenRequestIsValid_ShouldCreateUserAndReturnSummary() {
        SignupRequest request = new SignupRequest(
                "new@example.com", "NewUser", "John", null, "Doe", "password123"
        );

        User mappedUser = new User();
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("new@example.com");

        UserSummaryResponse expectedResponse = new UserSummaryResponse(1L, "NewUser", "John Doe");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("NewUser")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-pass");
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toSummary(savedUser)).thenReturn(expectedResponse);

        UserSummaryResponse response = authService.signup(request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(userRepository).save(mappedUser);
        assertThat(mappedUser.getRole()).isEqualTo(Role.USER);
        assertThat(mappedUser.getPassword()).isEqualTo("encoded-pass");
    }

    @Test
    void signup_WhenEmailAlreadyExists_ShouldThrowResourceAlreadyExists() {
        SignupRequest request = new SignupRequest(
                "existing@example.com", "User", "John", null, "Doe", "pass"
        );

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("email");

        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void signup_WhenUsernameAlreadyExists_ShouldThrowResourceAlreadyExists() {
        SignupRequest request = new SignupRequest(
                "new@example.com", "TakenUser", "John", null, "Doe", "pass"
        );

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("TakenUser")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("User")
                .hasMessageContaining("username");

        verify(userRepository, never()).save(any());
    }


    @Test
    void refreshToken_WhenTokenValid_ShouldReturnNewAccessToken() {
        String requestToken = "valid-refresh-token";
        TokenRefreshRequest request = new TokenRefreshRequest(requestToken);

        User user = new User();
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(requestToken);
        refreshToken.setUser(user);

        when(refreshTokenService.findByToken(requestToken)).thenReturn(refreshToken);
        doNothing().when(refreshTokenService).verifyExpiration(refreshToken);
        when(tokenService.generateToken(any(Authentication.class))).thenReturn("new-access-token");
        when(jwtProperties.expirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.refreshToken(request);

        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo(requestToken);
    }

    @Test
    void refreshToken_WhenTokenNotFound_ShouldThrowException() {
        TokenRefreshRequest request = new TokenRefreshRequest("unknown-token");
        when(refreshTokenService.findByToken("unknown-token"))
                .thenThrow(new ResourceNotFoundException("Token not found"));

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void logout_WhenUserExists_ShouldDeleteRefreshToken() {
        String email = "user@example.com";
        User user = new User();
        user.setId(123L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authService.logout(email);

        verify(refreshTokenService).deleteByUserId(123L);
    }

    @Test
    void logout_WhenUserNotFound_ShouldThrowResourceNotFound() {
        String email = "ghost@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}