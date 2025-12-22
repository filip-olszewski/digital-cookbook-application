package io.github.filipolszewski.cookbook;

import io.github.filipolszewski.cookbook.config.properties.CorsProperties;
import io.github.filipolszewski.cookbook.config.properties.JwtProperties;
import io.github.filipolszewski.cookbook.config.properties.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({RsaKeyProperties.class, JwtProperties.class, CorsProperties.class})
public class DigitalCookbookApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalCookbookApiApplication.class, args);
	}

}
