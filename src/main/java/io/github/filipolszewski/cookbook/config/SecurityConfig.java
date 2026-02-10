package io.github.filipolszewski.cookbook.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.filipolszewski.cookbook.config.properties.CorsProperties;
import io.github.filipolszewski.cookbook.config.properties.RsaKeyProperties;
import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.security.CustomAccessDeniedHandler;
import io.github.filipolszewski.cookbook.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RsaKeyProperties rsaKeyProperties;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // Public Endpoints (Reading + Auth)
                .requestMatchers(ApiConstants.API_V1 + "/auth/**").permitAll()

                // For demonstration
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                .requestMatchers(HttpMethod.GET,
                        ApiConstants.API_V1 + "/categories/**",
                        ApiConstants.API_V1 + "/ingredients/**",
                        ApiConstants.API_V1 + "/tags/**",
                        ApiConstants.API_V1 + "/recipes/**"
                ).permitAll()

                // Actuator
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/actuator/metrics").hasAuthority("ADMIN")

                // Only authenticated user can see their own profile
                .requestMatchers(ApiConstants.API_V1 + "/users/me").authenticated()
                .requestMatchers(HttpMethod.GET, ApiConstants.API_V1 + "/users/**").permitAll()

                // Admin only
                .requestMatchers(HttpMethod.POST,
                        ApiConstants.API_V1 + "/ingredients/**",
                        ApiConstants.API_V1 + "/tags/**",
                        ApiConstants.API_V1 + "/categories/**"
                ).hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.PATCH,
                        ApiConstants.API_V1 + "/ingredients/**",
                        ApiConstants.API_V1 + "/tags/**",
                        ApiConstants.API_V1 + "/categories/**"
                ).hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.DELETE,
                        ApiConstants.API_V1 + "/ingredients/**",
                        ApiConstants.API_V1 + "/tags/**",
                        ApiConstants.API_V1 + "/categories/**"
                ).hasAuthority("ADMIN")

                // Authenticated user actions
                .requestMatchers(HttpMethod.POST, ApiConstants.API_V1 + "/recipes").authenticated()
                .requestMatchers(HttpMethod.PATCH, ApiConstants.API_V1 + "/recipes/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, ApiConstants.API_V1 + "/recipes/**").authenticated()

                .requestMatchers(HttpMethod.POST, ApiConstants.API_V1 + "/recipes/*/reviews").authenticated()
                .requestMatchers(HttpMethod.PATCH, ApiConstants.API_V1 + "/recipes/*/reviews/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, ApiConstants.API_V1 + "/recipes/*/reviews/**").authenticated()

                .requestMatchers(HttpMethod.POST, ApiConstants.API_V1 + "/recipes/*/favourite").authenticated()
                .requestMatchers(HttpMethod.DELETE, ApiConstants.API_V1 + "/recipes/*/favourite").authenticated()

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(corsProperties.allowedMethods());
        configuration.setAllowedHeaders(corsProperties.allowedHeaders());
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtGrantedAuthoritiesConverter();

        converter.setAuthorityPrefix("");
        converter.setAuthoritiesClaimName("scope");

        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtConverter;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeyProperties.publicKey())
                .privateKey(rsaKeyProperties.privateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

}
