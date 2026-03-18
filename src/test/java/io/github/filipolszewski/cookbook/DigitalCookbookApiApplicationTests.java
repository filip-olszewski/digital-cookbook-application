package io.github.filipolszewski.cookbook;

import io.github.filipolszewski.cookbook.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Disabled("Skipping integration tests until core API features are finalized")
@ActiveProfiles("test")
class DigitalCookbookApiApplicationTests extends BaseIntegrationTest {

	@Test
	void contextLoads() {
	}

}
