package io.github.filipolszewski.cookbook.security.service;

import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtProperties jwtProperties;

    public String generateToken(Authentication authentication) {
        log.debug("Generating JWT access token for principal: {}", authentication.getName());

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtProperties.expirationSeconds()))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        log.debug("Successfully generated JWT access token for principal: {}", authentication.getName());

        return token;
    }

}