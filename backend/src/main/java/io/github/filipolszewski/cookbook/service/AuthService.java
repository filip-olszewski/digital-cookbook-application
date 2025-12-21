package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.dto.auth.AuthResponse;
import io.github.filipolszewski.cookbook.dto.auth.LoginRequest;
import io.github.filipolszewski.cookbook.dto.auth.SignupRequest;
import io.github.filipolszewski.cookbook.dto.user.UserSummaryResponse;
import io.github.filipolszewski.cookbook.exception.ResourceAlreadyExistsException;
import io.github.filipolszewski.cookbook.mapper.UserMapper;
import io.github.filipolszewski.cookbook.model.entity.User;
import io.github.filipolszewski.cookbook.model.enumeration.Role;
import io.github.filipolszewski.cookbook.repository.UserRepository;
import io.github.filipolszewski.cookbook.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JwtProperties jwtProperties;

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = tokenService.generateToken(auth);
        return new AuthResponse(token, "Bearer", jwtProperties.expirationSeconds());
    }

    @Transactional
    public UserSummaryResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("User with this address email is already registered!");
        }

        if(userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExistsException("User with this username is already registered!");
        }

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.password()));

        return userMapper.toSummary(userRepository.save(user));
    }

}
