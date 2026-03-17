package io.github.filipolszewski.cookbook.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.filipolszewski.cookbook.config.properties.CorsProperties;
import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.config.properties.RsaKeyProperties;
import io.github.filipolszewski.cookbook.constant.ApiConstants;
import io.github.filipolszewski.cookbook.security.CustomAccessDeniedHandler;
import io.github.filipolszewski.cookbook.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties({
    RsaKeyProperties.class,
    JwtProperties.class,
    CorsProperties.class
})
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

                // S3 file upload
                .requestMatchers(ApiConstants.API_V1 + "/files/upload-url").authenticated()
                .requestMatchers(ApiConstants.API_V1 + "/files/download-url").permitAll()

                .requestMatchers(HttpMethod.GET,
                        ApiConstants.API_V1 + "/categories/**",
                        ApiConstants.API_V1 + "/ingredients/**",
                        ApiConstants.API_V1 + "/tags/**",
                        ApiConstants.API_V1 + "/recipes/**"
                ).permitAll()

                // Actuator
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasAuthority("ADMIN")

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
                .jwt(jwt -> {
                            try {
                                jwt.decoder(jwtDecoder())
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
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
    public JwtDecoder jwtDecoder() throws Exception {
        return NimbusJwtDecoder.withPublicKey(getPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        JWK jwk = new RSAKey.Builder(getPublicKey())
                .privateKey(getPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private RSAPublicKey getPublicKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(rsaKeyProperties.publicKey());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    private RSAPrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(rsaKeyProperties.privateKey());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

}
