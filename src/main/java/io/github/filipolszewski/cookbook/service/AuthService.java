package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.dto.auth.AuthResponse;
import io.github.filipolszewski.cookbook.dto.auth.LoginRequest;
import io.github.filipolszewski.cookbook.dto.auth.TokenRefreshRequest;
import io.github.filipolszewski.cookbook.dto.auth.SignupRequest;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.exception.ResourceNotFoundException;
import io.github.filipolszewski.cookbook.mapper.UserMapper;
import io.github.filipolszewski.cookbook.model.entity.RefreshToken;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.CustomUserPrincipal;
import io.github.filipolszewski.cookbook.security.service.RefreshTokenService;
import io.github.filipolszewski.cookbook.security.service.TokenService;
import io.github.filipolszewski.cookbook.util.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login request for user: {}", request.email());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();

        String accessToken = tokenService.generateToken(auth);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(principal.getId());

        log.info("User {} successfully logged in", request.email());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtProperties.expirationSeconds()
        );
    }

    @Transactional
    public UserSummaryResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        log.info("Processing signup request for email: {}", email);

        if(userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(User.class, "email", email)
            );
        }

        if(userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExistsException(
                    ErrorMessageUtil.exists(User.class, "username", request.username())
            );
        }

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        log.info("Successfully registered new user with ID: {} and email: {}", savedUser.getId(), email);

        return userMapper.toSummary(savedUser);
    }

    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        log.info("Processing token refresh request");

        RefreshToken token = refreshTokenService.findByToken(request.token());
        refreshTokenService.verifyExpiration(token);
        User user = token.getUser();
        String accessToken = tokenService.generateToken(user);

        log.info("Successfully refreshed token for user ID: {}", user.getId());

        return new AuthResponse(
                accessToken,
                request.token(),
                "Bearer",
                jwtProperties.expirationSeconds()
        );
    }

    @Transactional
    public void logout(String email) {
        log.info("Processing logout request for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenService.deleteByUserId(user.getId());

        log.info("User {} successfully logged out", email);
    }
}