package io.github.filipolszewski.cookbook.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.security.CustomUserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void generateToken_ShouldCreateValidClaims_AndReturnTokenString() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@example.com");

        CustomUserPrincipal principal = mock(CustomUserPrincipal.class);
        when(principal.getId()).thenReturn(5L);
        when(auth.getPrincipal()).thenReturn(principal);

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        doReturn(authorities).when(auth).getAuthorities();

        when(jwtProperties.expirationSeconds()).thenReturn(3600L);

        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getTokenValue()).thenReturn("mocked-jwt-token-string");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwtMock);

        String result = tokenService.generateToken(auth);

        assertThat(result).isEqualTo("mocked-jwt-token-string");

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        assertThat(claims.getSubject()).isEqualTo("test@example.com");
        assertThat((String) claims.getClaim("iss")).isEqualTo("self");
        assertThat((String) claims.getClaim("scope")).isEqualTo("ROLE_USER ROLE_ADMIN");
        assertThat((Long) claims.getClaim("userId")).isEqualTo(5L);
        assertThat(claims.getExpiresAt()).isAfter(Instant.now());
    }
}